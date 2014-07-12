package edu.hyu.cs.jcrux.tide;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.hyu.cs.flags.Flags;
import edu.hyu.cs.jcrux.Carp;
import edu.hyu.cs.jcrux.CruxApplication;
import edu.hyu.cs.jcrux.CruxUtils;
import edu.hyu.cs.jcrux.Objects.COMMAND_T;
import edu.hyu.cs.jcrux.Objects.DECOY_TYPE;
import edu.hyu.cs.jcrux.Objects.DIGEST;
import edu.hyu.cs.jcrux.Objects.ENZYME;
import edu.hyu.cs.jcrux.Objects.MASS_TYPE;
import edu.hyu.cs.jcrux.Peptide;
import edu.hyu.cs.jcrux.ProteinPeptideIterator;
import edu.hyu.cs.jcrux.tide.VariableModTable.MODS_SPEC_TYPE;
import edu.hyu.cs.jcrux.tide.records.HeadedRecordWriter;
import edu.hyu.cs.pb.HeaderPB.Header;
import edu.hyu.cs.pb.PeptidesPB.AuxLocation;
import edu.hyu.cs.pb.RawProteinsPB.Protein;
import edu.hyu.cs.types.Pair;
import edu.hyu.cs.types.Pair2;

/**
 * @author HoonKim
 * 
 */
public class TideIndexApplication extends CruxApplication {

	private int maxMods;
	private String tmpfilePrefix;

	/**
	 * 펩타이드 저장하는 클래스 인듯.
	 * 
	 * @author HoonKim
	 * 
	 */
	protected class TideIndexPeptide {
		private double mMass;
		private int mLength;
		private int mProteinId;
		private int mProteinPos;

		// TODO 여기 문제 없는지 체크;
		String mResidues;

		boolean mDecoy;

		public TideIndexPeptide(double mass, int length, String proteinSeq,
				int proteinId, int proteinPos, boolean decoy) {
			mMass = mass;
			mLength = length;
			mProteinId = proteinId;
			mProteinPos = proteinPos;

			mResidues = proteinSeq.substring(proteinPos, proteinPos + length);
		}

		TideIndexPeptide(final TideIndexPeptide other) {
			mMass = other.getMass();
			mLength = other.getLength();
			mProteinId = other.getProteinId();
			mProteinPos = other.getProteinPos();
			mResidues = other.getSequence();
			mDecoy = other.isDecoy();
		}

		public double getMass() {
			return mMass;
		}

		public int getLength() {
			return mLength;
		}

		public int getProteinId() {
			return mProteinId;
		}

		public int getProteinPos() {
			return mProteinPos;
		}

		public String getSequence() {
			return mResidues;
		}

		public boolean isDecoy() {
			return mDecoy;
		}

		public boolean isBigger(final TideIndexPeptide peptide) {
			if (isEqual(peptide)) {
				return false;
			} else if (mMass != peptide.getMass()) {
				return mMass > peptide.getMass();
			} else if (mLength != peptide.getLength()) {
				return mLength > peptide.getLength();
			} else {
				int compareResult = mResidues.compareTo(peptide.getSequence());
				if (compareResult != 0) {
					return compareResult > 0;
				}
			}
			return false;
		}

		public boolean isEqual(final TideIndexPeptide peptide) {
			return (mMass == peptide.getMass()
					&& mLength == peptide.getLength() && mResidues
						.equals(peptide.getSequence()));
		}
	}

	/**
	 * Sequence, start location
	 */
	private class PeptideInfo extends Pair2<String, Integer> {

		protected PeptideInfo(String first, int second) {
			super(first, second);
		}

		private PeptideInfo() {
			super();
		}

	}

	private class ProteinInfo {
		private String name;
		private String sequence;

		ProteinInfo(final String proteinName, final String proteinSequence) {
			name = proteinName;
			sequence = proteinSequence;
		}

		public String getName() {
			return name;
		}

		public String getSequence() {
			return sequence;
		}
	}

	private class TargetInfo {
		ProteinInfo proteinInfo;
		int start;
		double mass;

		TargetInfo(final ProteinInfo protein, final int startLoc,
				final double pepMass) {
			proteinInfo = protein;
			start = startLoc;
			mass = pepMass;
		}

		public ProteinInfo getProteinInfo() {
			return proteinInfo;
		}

		public int getStart() {
			return start;
		}

		public double getMass() {
			return mass;
		}

	}

	private void fastaToPb(final String commandLine, final ENZYME enzyme,
			final DIGEST digestion, final int missedCleavages,
			final double minMass, final double maxMass, final int minLength,
			final int maxLength, MASS_TYPE massType, DECOY_TYPE decoyType,
			final String fasta, final String proteinPbFile,
			final Header.Builder outProteinPbHeader,
			final LinkedList<TideIndexPeptide> outPeptideHeap,
			final LinkedList<String> outProteinSequences,
			final FileOutputStream decoyFasta) {

		// 2014-07-28
		String decoyPrefix = Flags.getStringParameter("decoy-prefix");

		outProteinPbHeader.clear();
		outProteinPbHeader.setFileType(Header.FileType.RAW_PROTEINS);
		outProteinPbHeader.setCommandLine(commandLine);

		/* 이렇게 구현하는게 맞나 싶음. */
		Header.Source.Builder headerSource = outProteinPbHeader
				.addSourceBuilder();
		headerSource.setFilename(AbsPath.absPath(fasta));
		headerSource.setFiletype("fasta");
		outProteinPbHeader.addSource(headerSource.build());

		outPeptideHeap.clear();
		outProteinSequences.clear();

		// Todo outProteinPbHeader.build()를 인스턴스로로 만들 필요가 있을지도..
		HeadedRecordWriter proteinWriter = new HeadedRecordWriter(
				proteinPbFile, outProteinPbHeader.build());
		BufferedReader fastaStream = null;
		try {
			fastaStream = new BufferedReader(new FileReader(new File(fasta)));
		} catch (FileNotFoundException e) {
			Carp.carp(Carp.CARP_DEBUG, "Cannot open fasta in fastatopb()");
		}
		Protein.Builder pbProteinBuilder = Protein.newBuilder();

		int curProtein = -1;
		LinkedList<Pair2<ProteinInfo, LinkedList<PeptideInfo>>> cleavedPeptideInfo = new LinkedList<Pair2<ProteinInfo, LinkedList<PeptideInfo>>>();
		TreeSet<String> setTargets = new TreeSet<String>();
		TreeMap<String, TargetInfo> targetInfo = new TreeMap<String, TargetInfo>();

		Pair2<String, String> proteinNameAndSequence = Pair2.of(null, null);

		int targetGenerated = 0;

		while (getNextProtein(fastaStream, proteinNameAndSequence)) {

			System.out.println(curProtein + 1);
			outProteinSequences.addLast(proteinNameAndSequence.second);

			String proteinName = proteinNameAndSequence.first;
			String proteinSequence = proteinNameAndSequence.second;

			ProteinInfo proteinInfoTemp = new ProteinInfo(proteinName,
					proteinSequence);

			LinkedList<PeptideInfo> second = new LinkedList<PeptideInfo>();
			Pair2<ProteinInfo, LinkedList<PeptideInfo>> temp = Pair2.of(
					proteinInfoTemp, second);

			cleavedPeptideInfo.addLast(temp);

			ProteinInfo proteinInfo = cleavedPeptideInfo.getLast().first;
			LinkedList<PeptideInfo> cleavedPeptides = cleavedPeptideInfo
					.getLast().second;

			getPbProtein(++curProtein, proteinName, proteinSequence,
					pbProteinBuilder);
			proteinWriter.write(pbProteinBuilder.build());
			cleaveProtein(proteinSequence, enzyme, digestion, missedCleavages,
					minLength, maxLength, cleavedPeptides);
		}

		// TODO 442

	}

	private boolean getNextProtein(BufferedReader fasta,
			Pair2<String, String> proteinNameAndSequence) {

		proteinNameAndSequence.first = null;
		proteinNameAndSequence.second = "";

		// peek이 없어서 대신하기 위해 사용됨.

		try {
			if (!fasta.ready()) {
				System.out.println("not ready");
				return false;
			}

			String line = null;
			while ((line = fasta.readLine()) != null) {

				line = line.trim();

				if (proteinNameAndSequence.first == null) {
					if ((line != null) && (line.length() > 0)
							&& (line.charAt(0) == '>')) {
						proteinNameAndSequence.first = line.substring(1);
					}
				} else {
					proteinNameAndSequence.second += line;

					fasta.mark(1);
					char c = (char) fasta.read();
					fasta.reset();
					if (c == '>') {
						break;
					}
				}

			}

			if ((proteinNameAndSequence.second != null)
					&& proteinNameAndSequence.second.endsWith("*")) {
				// Remove the last character of the sequence if it is an
				// asterisk
				proteinNameAndSequence.second = proteinNameAndSequence.second
						.substring(0,
								proteinNameAndSequence.second.length() - 1);

			}

			if (proteinNameAndSequence.second == null) {
				Carp.carp(Carp.CARP_WARNING,
						"Found protein ID without sequence : %s",
						proteinNameAndSequence.first);
				proteinNameAndSequence.first = null;
				return false;
			}

		} catch (IOException e) {
			System.out.println("IO Exception");
			return false;
		}

		return true;
	}

	private void cleaveProtein(final String sequence, ENZYME enzyme,
			DIGEST digest, int missedCleavages, int minLength, int maxLength,
			LinkedList<PeptideInfo> outPeptides) {

		outPeptides.clear();

		if (enzyme == ENZYME.NO_ENZYME) {
			int pepStart = 0, nextPepStart = 0;
			int cleaveSites = 0;
			for (int i = 0; i < sequence.length(); i++) {
				// Determine if this is a valid cleavage position
				boolean cleavePos = ProteinPeptideIterator
						.validCleavagePosition(sequence.substring(i), enzyme);

				if ((i != (sequence.length() - 1)) && (!cleavePos)
						&& (digest == DIGEST.PARTIAL_DIGEST)) {
					// Partial digestion (not last AA or cleavage position), add
					// this peptide
					outPeptides.addLast(new PeptideInfo(sequence.substring(
							pepStart, i + 1 - pepStart), pepStart));
				} else if (cleavePos) {
					outPeptides.addLast(new PeptideInfo(sequence.substring(
							pepStart, i + 1 - pepStart), pepStart));
					if (++cleaveSites == 1) {
						nextPepStart = i + 1;
					}
					if (digest == DIGEST.PARTIAL_DIGEST) {
						for (int j = pepStart; j < nextPepStart; ++j) {
							outPeptides.addLast(new PeptideInfo(sequence
									.substring(j, i - j + 1), j));
						}
					}
					if (cleaveSites > missedCleavages) {
						pepStart = nextPepStart;
						i = pepStart - 1;
						cleaveSites = 0;
					}
				} else if ((i == (sequence.length() - 1)) && (cleaveSites > 0)
						&& (cleaveSites <= missedCleavages)) {
					outPeptides.addLast(new PeptideInfo(sequence
							.substring(pepStart), pepStart));
					if (digest == DIGEST.PARTIAL_DIGEST) {
						for (int j = pepStart + 1; j < nextPepStart; ++j) {
							outPeptides.addLast(new PeptideInfo(sequence
									.substring(j, i - j + 1), j));
						}
					}
					pepStart = nextPepStart;
					i = pepStart - 1;
					cleaveSites = 0;
				}
			}
			// Add the last peptide
			outPeptides.addLast(new PeptideInfo(sequence
					.substring(nextPepStart), nextPepStart));
			if (digest == DIGEST.PARTIAL_DIGEST) {
				// For partial digest, add peptides ending at last AA
				for (int j = pepStart + 1; j < sequence.length(); ++j) {
					outPeptides.addLast(new PeptideInfo(sequence.substring(j),
							j));
				}
			}
			
		}

	}

	private void writePeptidesAndAuxLocs(final String sequence,
			final MASS_TYPE massType) {
		// TODO 구현해.
	}

	private double calcPepMassTide(final String sequence, MASS_TYPE massType) {
		// TODO 구현해.
		return 0;
	}

	private void getPbProtein(final int id, final String name,
			final String residues, final Protein.Builder outPbProtein) {
		outPbProtein.clear();
		outPbProtein.setId(id);
		outPbProtein.setName(name);
		outPbProtein.setResidues(residues);
	}

	private void getPbPeptide(final int id, final TideIndexPeptide peptide,
			Peptide outPbPeptide) {
		// TODO 구현해.
	}

	private void addAuxLoc(final int proteinId, final int proteinPos,
			AuxLocation outAuxLoc) {

	}

	@Override
	public int main(String[] argv) {

		final String optionList[] = { "decoy-format", "decoy-prefix", "enzyme",
				"custom-enzyme", "digestion", "missed-cleavages", "max-length",
				"max-mass", "min-length", "min-mass", "monoisotopic-precursor",
				"mods-spec", "cterm-peptide-mods-spec",
				"nterm-peptide-mods-spec", "cterm-protein-mods-spec",
				"nterm-protein-mods-spec", "max-mods", "output-dir",
				"overwrite", "peptide-list", "parameter-file", "seed",
				// "PTMDB",
				"verbosity" };

		final String defaultCysteine = "C+57.0214637206";

		initialize(optionList, argv);

		Carp.carp(Carp.CARP_INFO, "Running tide-index");

		// Build command line string
		String cmdLine = "crux tide-index";
		for (int i = 1; i < argv.length; ++i) {
			cmdLine += " ";
			cmdLine += argv[i];
		}

		Flags.tmpFilePrefix = CruxUtils
				.makeFilePath("modified_peptides_partial_");

		// Get Options

		double minMass = Flags.getDoubleParameter("min-mass");
		double maxMass = Flags.getDoubleParameter("max-mass");
		int minLength = Flags.getIntParameter("min-length");
		int maxLength = Flags.getIntParameter("max-length");
		boolean monoisotopicPrecursor = Flags
				.getBooleanParameter("monoisotopic-precursor");
		Flags.MaxMods = Flags.getIntParameter("max-mods");
		MASS_TYPE mass_type = (monoisotopicPrecursor) ? MASS_TYPE.MONO
				: MASS_TYPE.AVERAGE;
		int missedCleavages = Flags.getIntParameter("missed-cleavages");
		DIGEST digestion = Flags.getDigestParameter("digestion");
		ENZYME enzyme = Flags.getEnzymeParameter("enzyme");

		if ((digestion != DIGEST.FULL_DIGEST)
				&& (digestion != DIGEST.PARTIAL_DIGEST)) {
			Carp.carp(Carp.CARP_FATAL,
					"'digestion' must be 'full-digest' or 'partial-digest'");
		}

		VariableModTable varModTable = new VariableModTable();
		String modsSpec;
		varModTable.clearTables();

		modsSpec = Flags.getStringParameter("mods-spec");
		if (modsSpec == null || !modsSpec.contains("C")) {
			modsSpec = (modsSpec == null) ? defaultCysteine : defaultCysteine
					+ "," + modsSpec;
			Carp.carp(Carp.CARP_DEBUG,
					"Using default cysteine mod '%s' ('%s')", defaultCysteine,
					modsSpec);
		}
		if (!varModTable.parse(modsSpec, null)) {
			Carp.carp(Carp.CARP_FATAL, "Error parsing mods");
		}

		modsSpec = Flags.getStringParameter("cterm-peptide-mods-spec");
		if (modsSpec != null) {
			if (!varModTable.parse(modsSpec, MODS_SPEC_TYPE.CTPEP)) {
				Carp.carp(Carp.CARP_FATAL,
						"Error parsing c-terminal peptide mods");
			}
		}
		modsSpec = Flags.getStringParameter("nterm-peptide-mods-spec");
		if (modsSpec != null) {
			if (!varModTable.parse(modsSpec, MODS_SPEC_TYPE.NTPEP)) {
				Carp.carp(Carp.CARP_FATAL,
						"Error parsing n-terminal peptide mods");
			}
		}
		modsSpec = Flags.getStringParameter("cterm-protein-mods-spec");
		if (modsSpec != null) {
			if (!varModTable.parse(modsSpec, MODS_SPEC_TYPE.CTPRO)) {
				Carp.carp(Carp.CARP_FATAL,
						"Error parsing c-terminal protein mods");
			}
		}
		modsSpec = Flags.getStringParameter("nterm-protein-mods-spec");
		if (modsSpec != null) {
			if (!varModTable.parse(modsSpec, MODS_SPEC_TYPE.NTPRO)) {
				Carp.carp(Carp.CARP_FATAL,
						"Error parsing n-terminal protein mods");
			}
		}

		varModTable.serializeUniqueDeltas();

		if (!MassConstants.init(varModTable.parsedModTable())) {
			Carp.carp(Carp.CARP_FATAL, "Error in MassConstants::Init");
		}

		DECOY_TYPE decoyType = Flags.getTideDecoyTypeParameter("decoy-foramt");
		String decoyPrefix = Flags.getStringParameter("decoy-prefix");

		// Set up output Paths.
		String fasta = argv[argv.length - 2];
		String index = argv[argv.length - 1];
		boolean overwrite = Flags.getBooleanParameter("overwrite");

		if (!CruxUtils.fileExists(fasta)) {
			Carp.carp(Carp.CARP_FATAL, "Fasta file %s does not exist", fasta);
		}

		String outProteins = index + "/" + "protix";
		String outPeptides = index + "/" + "pepix";
		String outAux = index + "/" + "auxlocs";
		String modlessPeptides = outPeptides + ".nomods.tmp";
		String peaklessPeptides = outPeptides + ".nopeaks.tmp";

		FileOutputStream outTargetList = null;
		FileOutputStream outDecoyList = null;

		if (Flags.getBooleanParameter("peptide-list")) {
			outTargetList = CruxUtils.createStreamInPath(
					CruxUtils.makeFilePath("tide-index.decoy.fasta"), null,
					overwrite);
			if (decoyType != DECOY_TYPE.NO_DECOYS) {
				outDecoyList = CruxUtils
						.createStreamInPath(CruxUtils
								.makeFilePath("tide-index.peptides.decoy.txt"),
								null, overwrite);
			}
		}

		// 디코이 만드는부분 생략함 176 줄.

		if (CruxUtils.createOutputDirectory(index, overwrite) != 0) {
			Carp.carp(Carp.CARP_FATAL, "Error creating index directory");
		} else if (CruxUtils.fileExists(outProteins)
				|| CruxUtils.fileExists(outPeptides)
				|| CruxUtils.fileExists(outAux)) {
			if (overwrite) {
				Carp.carp(Carp.CARP_DEBUG, "Cleaning old index files(s)");
				CruxUtils.fileDelete(outProteins);
				CruxUtils.fileDelete(outPeptides);
				CruxUtils.fileDelete(outAux);
				CruxUtils.fileDelete(modlessPeptides);
				CruxUtils.fileDelete(peaklessPeptides);
			} else {
				Carp.carp(Carp.CARP_FATAL, "Index file(s) alreaday exists ,"
						+ "use --overwrite T or a different index name");
			}
		}

		// 198 라인 일단 보류.

		Carp.carp(Carp.CARP_INFO,
				"Reading %s and computing umodified peptides...", fasta);

		// TODO 안되면 여기를 의심.

		Header.Builder proteinPbHeader = Header.newBuilder();

		LinkedList<TideIndexPeptide> peptideHeap = new LinkedList<TideIndexPeptide>();
		// TODO TideIndexPeptide 구현.
		LinkedList<String> proteinSequences = new LinkedList<String>();
		fastaToPb(cmdLine, enzyme, digestion, missedCleavages, minMass,
				maxMass, minLength, maxLength, mass_type, decoyType, fasta,
				outProteins, proteinPbHeader, peptideHeap, proteinSequences,
				null);

		// Set up peptides header.

		Carp.carp(Carp.CARP_DEBUG, "디버그 종료.");

		// FileOutputStream outDecoyFasta = GenerateDecoys
		// .canGenerateDecoyProteins() ? CruxUtils.createStreamInPath(
		// CruxUtils.makeFilePath("tide-index.decoy.fasta"), null,
		// overwrite) : null;
		/* very important comments !! */

		return 0;
	}

	@Override
	public String getName() {
		return "tide-index";
	}

	@Override
	public COMMAND_T getCommand() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean needsOutputDirectory() {
		return true;
	}

	@Override
	public boolean hidden() {
		// TODO Auto-generated method stub
		return false;
	}

}
