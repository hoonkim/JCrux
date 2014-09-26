package edu.hyu.cs.jcrux.tide;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
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
import edu.hyu.cs.jcrux.ProteinPeptideIterator;
import edu.hyu.cs.jcrux.tide.VariableModTable.MODS_SPEC_TYPE;
import edu.hyu.cs.jcrux.tide.records.HeadedRecordReader;
import edu.hyu.cs.jcrux.tide.records.HeadedRecordWriter;
import edu.hyu.cs.pb.HeaderPB.Header;
import edu.hyu.cs.pb.HeaderPB.Header.PeptidesHeader;
import edu.hyu.cs.pb.HeaderPB.ModTable;
import edu.hyu.cs.pb.PeptidesPB.AuxLocation;
import edu.hyu.cs.pb.PeptidesPB.Location;
import edu.hyu.cs.pb.PeptidesPB.Peptide;
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
	protected class TideIndexPeptide implements Comparable<TideIndexPeptide> {
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

		@Override
		public int compareTo(TideIndexPeptide peptide) {
			if (isBigger(peptide))
				return -1;
			else if (isEqual(peptide)) {
				return 0;
			} else {
				return 1;
			}
		}
	}

	/**
	 * Sequence, start location
	 */
	private class PeptideInfo extends Pair<String, Integer> {

		protected PeptideInfo(String first, int second) {
			super(first, second);
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

			outProteinSequences.addLast(proteinNameAndSequence.second);

			String proteinName = proteinNameAndSequence.first.split("[ \t\n]")[0];
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

			// Write pb::Protein
			getPbProtein(++curProtein, proteinName, proteinSequence,
					pbProteinBuilder);
			Protein protein = pbProteinBuilder.build();
			proteinWriter.write(protein);
			cleaveProtein(proteinSequence, enzyme, digestion, missedCleavages,
					minLength, maxLength, cleavedPeptides);

			for (int i = 0; i < cleavedPeptides.size();) {
				String cleavedSequence = cleavedPeptides.get(i).first;
				double pepMass = calcPepMassTide(cleavedSequence, massType);

				if (pepMass < 0.0) {
					Carp.carp(
							Carp.CARP_WARNING,
							"Ignoring invalid sequence <%s> who has pepMass <%.5f>",
							cleavedSequence, pepMass);
					cleavedPeptides.remove(i);
					continue;
				} else if (pepMass < minMass || pepMass > maxMass) {
					++i;
					continue;
				}
				int startLoc = cleavedPeptides.get(i).second;
				int pepLen = cleavedSequence.length();
				// Add target to heap

				TideIndexPeptide pepTarget = new TideIndexPeptide(pepMass,
						pepLen, proteinSequence, curProtein, startLoc, false);
				outPeptideHeap.addLast(pepTarget);

				// FIXME ?? 이부분 이상함.
				++targetGenerated;
				++i;

			}

		}
		proteinWriter.finish();
		Collections.sort(outPeptideHeap);

		Carp.carp(Carp.CARP_DEBUG, "FASTA produced %d targets", targetGenerated);

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

		if (enzyme != ENZYME.NO_ENZYME) {
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
							pepStart, i + 1), pepStart));
				} else if (cleavePos) {
					outPeptides.addLast(new PeptideInfo(sequence.substring(
							pepStart, i + 1), pepStart));
					if (++cleaveSites == 1) {
						nextPepStart = i + 1;
					}
					if (digest == DIGEST.PARTIAL_DIGEST) {
						for (int j = pepStart; j < nextPepStart; ++j) {
							outPeptides.addLast(new PeptideInfo(sequence
									.substring(j, i + 1), j));
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
									.substring(j, i + 1), j));
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

			// Erase peptides that don't meet length requirement
			for (int i = 0; i < outPeptides.size();) {
				PeptideInfo pi = outPeptides.get(i);

				if (pi.first.length() < minLength
						|| pi.first.length() > maxLength) {
					outPeptides.remove(i);
				} else {
					// FIXME 이거 왜 있음?
					++i;
				}
			}
		} else {
			// No enzyme
			// Get all substrings min <= length <= max
			for (int i = 0; i < sequence.length(); ++i) {
				for (int j = minLength; (i + j) <= sequence.length()
						&& j <= maxLength; ++j) {
					outPeptides.addLast(new PeptideInfo(sequence.substring(i, i
							+ j), i));
				}
			}
		}

	}

	private void writePeptidesAndAuxLocs(
			LinkedList<TideIndexPeptide> peptideHeap,
			Pair2<String, String> peptideAndAuxLocsPbFile,
			Header.Builder pbHeader) {
		// Check header
		if (pbHeader.getSourceCount() != 1) {
			Carp.carp(Carp.CARP_FATAL,
					"pbHeader had a number of source other than 1");
		}
		System.out.println("size :::: " + peptideHeap.size());

		Header.Source.Builder headerSource = pbHeader.getSourceBuilder(0);
		if (!headerSource.hasFilename() || headerSource.hasFiletype()) {
			Carp.carp(Carp.CARP_FATAL, "pbHeader source invalid");
		}

		String proteinsFile = headerSource.getFilename();

		LinkedList<Protein> proteins = new LinkedList<Protein>();
		Header proteinsHeader;

		Carp.carp(Carp.CARP_INFO, "Reading proteins");

		boolean succeed = true;

		HeadedRecordReader reader = new HeadedRecordReader(proteinsFile,
				Header.getDefaultInstance());
		proteinsHeader = reader.getHeader();
		System.out.println(proteinsHeader.getFileType().toString());

		while (!reader.done()) {
			Protein protein = reader.read();
			proteins.addLast(protein);
			// System.out.println(protein.getId());

		}
		if (!reader.ok()) {
			System.out.println("ahng?");
			proteins.clear();
			succeed = false;
		}

		if (!succeed) {
			Carp.carp(Carp.CARP_FATAL, "Error reading proteins from %s",
					proteinsFile);
		} else if (proteinsHeader.getFileType() != Header.FileType.RAW_PROTEINS) {
			Carp.carp(Carp.CARP_FATAL, "Proteins file %s had invalid type",
					proteinsFile);
		}

		// clean up 생략

		headerSource.setHeader(proteinsHeader);

		if (!pbHeader.hasPeptidesHeader()) {
			Carp.carp(Carp.CARP_FATAL,
					"pbHeader doeasn't have peptide heap header");
		}

		PeptidesHeader.Builder settings = pbHeader.getPeptidesHeaderBuilder(); // builder
		// 만들었음.

		if ((!settings.hasEnzyme()) || settings.getEnzyme().isEmpty()) {
			Carp.carp(Carp.CARP_FATAL, "Enzyme setting error");
		}

		pbHeader.setFileType(Header.FileType.PEPTIDES);

		Header.PeptidesHeader.Builder tempPeptidesHeader = pbHeader
				.getPeptidesHeaderBuilder();
		tempPeptidesHeader.setHasPeaks(false);
		// decoy type 정하는거 일단 보류
		pbHeader.setPeptidesHeader(tempPeptidesHeader);
		HeadedRecordWriter peptideWriter = new HeadedRecordWriter(
				peptideAndAuxLocsPbFile.first, pbHeader.build()); // put header
																	// in
																	// outfile

		// Create the auxiliary locations header and writer

		Header.Builder auxLocsHeader = Header.newBuilder();
		auxLocsHeader.setFileType(Header.FileType.AUX_LOCATIONS);
		Header.Source.Builder auxLocsSource = auxLocsHeader.addSourceBuilder();
		// FIXME 여기도 뭔가 이상한데? 왜 peptidePbFile이야?
		auxLocsSource.setFiletype(peptideAndAuxLocsPbFile.first);
		auxLocsSource.setHeader(pbHeader.clone());
		HeadedRecordWriter auxLocWriter = new HeadedRecordWriter(
				peptideAndAuxLocsPbFile.second, auxLocsHeader.build());

		AuxLocation.Builder pbAuxLoc = AuxLocation.newBuilder();
		Peptide.Builder pbPeptide = Peptide.newBuilder();

		int auxLocIdx = -1;
		Carp.carp(Carp.CARP_DEBUG, "%d peptides in heap", peptideHeap.size());
		int count = 0;
		Collections.sort(peptideHeap);

		while (!peptideHeap.isEmpty()) {
			TideIndexPeptide curPeptide = peptideHeap.getLast();
			peptideHeap.removeLast();
			// For duplicate peptides we only record the location
			while ((!peptideHeap.isEmpty())
					&& (peptideHeap.getLast().isEqual(curPeptide))) {
				Location.Builder location = pbAuxLoc.addLocationBuilder();
				location.setProteinId(peptideHeap.getLast().getProteinId());
				location.setPos(peptideHeap.getLast().getProteinPos());
				peptideHeap.removeLast();
			}
			getPbPeptide(count, curPeptide, pbPeptide);

			// Not all peptides have aux locations associated with them. Check
			// to see if GetGroup added any locations to aux_location. If yes,
			// only then assign the corresponding array index to the peptide and
			// write it out.
			if (pbAuxLoc.getLocationCount() > 0) {

				pbPeptide.setAuxLocationsIndex(++auxLocIdx);
				auxLocWriter.write(pbAuxLoc.build());
				pbAuxLoc.clear();
			}
			peptideWriter.write(pbPeptide.build());

			if (++count % 100000 == 0) {
				Carp.carp(Carp.CARP_INFO, "Wrote %d peptides, %d  auxlocs",
						count, auxLocIdx);
			}
		}
		peptideWriter.finish();
		auxLocWriter.finish();

	}

	boolean readRecordsToLinkedList(LinkedList<Protein> vec,
			final String fileName, Header.Builder header) {
		HeadedRecordReader reader = new HeadedRecordReader(fileName,
				header.build());
		while (!reader.done()) {
			Protein.Builder proteinBuilder = Protein.newBuilder();
			Protein protein = proteinBuilder.build();
			reader.read();
			vec.addLast(protein);
		}
		if (!reader.ok()) {
			return false;
		}
		return true;
	}

	private double calcPepMassTide(final String sequence, MASS_TYPE massType) {

		int mass = 0;
		int aaMass = 0;

		if (massType == MASS_TYPE.AVERAGE) {
			mass = MassConstants.FIXP_AVG_H2O;
			for (int i = 0; i < sequence.length(); ++i) {
				aaMass = MassConstants.FIXP_AVG_TABLE[sequence.charAt(i)];
				if (aaMass == 0) {
					return -1;
				}
				mass += aaMass;
			}

		} else if (massType == MASS_TYPE.MONO) {
			mass = MassConstants.FIXP_MONO_H2O;
			for (int i = 0; i < sequence.length(); ++i) {
				aaMass = MassConstants.FIXP_MONO_TABLE[sequence.charAt(i)];
				if (aaMass == 0) {
					return -1;
				}
				mass += aaMass;
			}

		} else {
			Carp.carp(Carp.CARP_FATAL, "Invalid mass type");
		}

		return MassConstants.toDouble(mass);
	}

	private void getPbProtein(final int id, final String name,
			final String residues, final Protein.Builder outPbProtein) {
		outPbProtein.clear();
		outPbProtein.setId(id);
		outPbProtein.setName(name);
		outPbProtein.setResidues(residues);
	}

	private void getPbPeptide(final int id, final TideIndexPeptide peptide,
			Peptide.Builder outPbPeptide) {
		outPbPeptide.clear();
		outPbPeptide.setId(id);
		outPbPeptide.setMass(peptide.getMass());
		outPbPeptide.setLength(peptide.getLength());
		Location.Builder firstLocation = outPbPeptide.getFirstLocationBuilder();
		firstLocation.setProteinId(peptide.getProteinId());
		firstLocation.setPos(peptide.getProteinPos());

		outPbPeptide.setIsDecoy(peptide.isDecoy());

	}

	private void addAuxLoc(final int proteinId, final int proteinPos,
			AuxLocation.Builder outAuxLoc) {
		Location.Builder location = outAuxLoc.addLocationBuilder();
		location.setProteinId(proteinId);
		location.setPos(proteinPos);
		outAuxLoc.addLocation(location);
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
		if (minMass == 0) {
			minMass = 200;
		}
		double maxMass = Flags.getDoubleParameter("max-mass");
		if (maxMass == 0) {
			maxMass = 7200;
		}
		int minLength = Flags.getIntParameter("min-length");
		if (minLength == 0) {
			minLength = 6;
		}
		int maxLength = Flags.getIntParameter("max-length");
		if (maxLength == 0) {
			maxLength = 50;
		}
		boolean monoisotopicPrecursor = Flags
				.getBooleanParameter("monoisotopic-precursor");
		monoisotopicPrecursor = true;

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
				//CruxUtils.fileDelete(outProteins);
				CruxUtils.fileDelete(outPeptides);
				//CruxUtils.fileDelete(outAux);
				//CruxUtils.fileDelete(modlessPeptides);
				//CruxUtils.fileDelete(peaklessPeptides);
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

		System.out.println("fastatopb start : " + Flags.getTime());

//		fastaToPb(cmdLine, enzyme, digestion, missedCleavages, minMass,
//				maxMass, minLength, maxLength, mass_type, decoyType, fasta,
//				outProteins, proteinPbHeader, peptideHeap, proteinSequences,
//				null);

		System.out.println("fastatopb end : " + Flags.getTime());

		Header.Builder headerWithMods = Header.newBuilder();

		// Set up peptides header
		Header.PeptidesHeader.Builder pepHeader = headerWithMods.getPeptidesHeaderBuilder();
		pepHeader.clear();
		pepHeader.setMinMass(minMass);
		pepHeader.setMaxMass(maxMass);
		pepHeader.setMinLength(minLength);
		pepHeader.setMaxLength(maxLength);
		pepHeader.setMonoisotopicPrecursor(monoisotopicPrecursor);
		pepHeader.setEnzyme(enzyme.toString());

		// FIXME 이부분 모호함.
		if (enzyme.toString() != "none") {
			pepHeader.setFullDigestion(digestion == DIGEST.FULL_DIGEST);
			pepHeader.setMaxMissedCleavages(missedCleavages);
		}

		// FIXME 맞는지 확신은 없음.
		pepHeader.setMods(varModTable.parsedModTable());

		//headerWithMods.setPeptidesHeader(pepHeader);

		headerWithMods.setFileType(Header.FileType.PEPTIDES);
		headerWithMods.setCommandLine(cmdLine);

		Header.Source.Builder source = headerWithMods.addSourceBuilder();
		source.setHeader(headerWithMods.build());
		source.setFilename(AbsPath.absPath(outProteins));
		// headerWithMods = headerWithMods.addSource(source.build());

		Header.Builder headerNoMods = headerWithMods.clone();
		PeptidesHeader.Builder delPeptideHeader = headerNoMods.getPeptidesHeaderBuilder();
		ModTable.Builder del = delPeptideHeader.getModsBuilder();
		del.clearVariableMod();
		del.clearUniqueDeltas();

		//delPeptideHeader.setMods(del);
		//headerNoMods.setPeptidesHeader(delPeptideHeader);

		boolean needMods = varModTable.uniqueDeltaSize() > 0;

		String basicPeptides = (needMods) ? modlessPeptides : peaklessPeptides;
		Carp.carp(Carp.CARP_DEBUG, "basicPeptides=%s", basicPeptides);
		System.out
				.println("writePeptidesAndAuxLocs start : " + Flags.getTime());
//		writePeptidesAndAuxLocs(peptideHeap, Pair2.of(basicPeptides, outAux),
//				headerNoMods);
		System.out.println("writePeptidesAndAuxLocs end : " + Flags.getTime());

		LinkedList<Protein> proteins = new LinkedList<Protein>();

		boolean succeed = true;

		HeadedRecordReader reader = new HeadedRecordReader(outProteins,
				Header.getDefaultInstance());

		while (!reader.done()) {
			Protein protein = reader.read();
			proteins.addLast(protein);

		}
		if (!reader.ok()) {
			proteins.clear();
			succeed = false;
		}
		if (!succeed) {
			Carp.carp(Carp.CARP_FATAL, "Error reading proteins file");
		}

		if (needMods) {
			Carp.carp(Carp.CARP_INFO, "Computing modified peptides");
			HeadedRecordReader ㄲreader = new HeadedRecordReader(
					modlessPeptides, null);

		}

		// TODO out target list 구현 266~375 line.

		Carp.carp(Carp.CARP_INFO, "Precomputing theoretical spectra...");
		
		System.out.println("addTheoriticalPeaks start : " + Flags.getTime());
		addTheoriticalPeaks(proteins, peaklessPeptides, outPeptides);
		System.out.println("addTheoriticalPeaks end : " + 14246);
		

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
		return false;
	}

	void addTheoriticalPeaks(LinkedList<Protein> proteins,
			String inputFileName, String outputFileName) {
		Header origHeader = null;
		HeadedRecordReader reader = new HeadedRecordReader(inputFileName,
				Header.getDefaultInstance());
		origHeader = reader.getHeader();

		MassConstants.init(origHeader.getPeptidesHeader().getMods());

		Header.Builder newHeader = Header.newBuilder();
		newHeader.setFileType(Header.FileType.PEPTIDES);
		newHeader.setPeptidesHeader(origHeader.getPeptidesHeader());

		Header.PeptidesHeader.Builder subHeader = newHeader.getPeptidesHeaderBuilder();
		subHeader.setHasPeaks(true);
		Header.Source.Builder source = newHeader.addSourceBuilder();
		source.setHeader(origHeader);
		source.setFilename(AbsPath.absPath(inputFileName));

		HeadedRecordWriter writer = new HeadedRecordWriter(outputFileName,
				newHeader.build());

		final int workspaceSize = 2000;

		TheoreticalPeakSetDiff workspace = new TheoreticalPeakSetDiff(
				workspaceSize);

		int count = 0;

		while (!reader.done()) {
			if ((count++ % 100000) == 0) {
				System.out.println("readcount : " + count);
			}

			Peptide.Builder pbPeptide = Peptide
					.newBuilder(reader.readPeptide());
			edu.hyu.cs.jcrux.tide.Peptide peptide = new edu.hyu.cs.jcrux.tide.Peptide(
					pbPeptide.build(), proteins);
			workspace.clear();
			peptide.computeTheoreticalPeaks(workspace);
			TheoreticalPeakArr peaksCharge1 = new TheoreticalPeakArr(2000);
			TheoreticalPeakArr peaksCharge2 = new TheoreticalPeakArr(2000);
			TheoreticalPeakArr negsCharge1 = new TheoreticalPeakArr(2000);
			TheoreticalPeakArr negsCharge2 = new TheoreticalPeakArr(2000);

			workspace.getPeaks(peaksCharge1, negsCharge1, peaksCharge2,
					negsCharge2);

			addPeaksToPB(pbPeptide, peaksCharge1, 1, false);
			addPeaksToPB(pbPeptide, peaksCharge2, 2, false);
			addPeaksToPB(pbPeptide, negsCharge1, 1, true);
			addPeaksToPB(pbPeptide, negsCharge2, 2, true);

			writer.write(pbPeptide.build());

		}

		writer.finish();
	}

	void addPeaksToPB(Peptide.Builder peptide, TheoreticalPeakArr peaks,
			int charge, boolean neg) {
		int lastCode = 0;

		for (int i = 0; i < peaks.size(); i++) {
			int delta = peaks.get(i).getCode() - lastCode;
			lastCode = peaks.get(i).getCode();
			if (neg) {
				if (charge == 1) {
					peptide.addNegPeak1(delta);
				} else {
					peptide.addNegPeak2(delta);
				}
			} else {
				if (charge == 1) {
					peptide.addPeak1(delta);
				} else {
					peptide.addPeak2(delta);
				}
			}
		}
	}
}
