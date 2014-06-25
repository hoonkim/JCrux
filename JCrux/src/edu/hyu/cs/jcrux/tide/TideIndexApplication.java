package edu.hyu.cs.jcrux.tide;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import edu.hyu.cs.jcrux.CruxApplication;
import edu.hyu.cs.jcrux.Objects.COMMAND_T;
import edu.hyu.cs.jcrux.Objects.DECOY_TYPE;
import edu.hyu.cs.jcrux.Objects.DIGEST;
import edu.hyu.cs.jcrux.Objects.ENZYME;
import edu.hyu.cs.jcrux.Objects.MASS_TYPE;
import edu.hyu.cs.jcrux.Peptide;
import edu.hyu.cs.jcrux.Protein;
import edu.hyu.cs.pb.HeaderPB.Header;
import edu.hyu.cs.pb.PeptidesPB.AuxLocation;

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
	private class PeptideInfo extends HashMap<String, Integer> {

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
			final Header outProteinPbHeader,
			final ArrayList<TideIndexPeptide> outPeptideHeap,
			final ArrayList<String> outProteinSequences,
			final FileOutputStream decoyFasta) {
		// TODO 구현해.
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
			final String residues, final Protein outPbProtein) {
		// TODO 구현해.
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

		// Crux command line parsing
		int numOptions = optionList.length;
		final String argList[] = { "protein fast file", "index name" };
		int numArgs = argList.length;

		initialize(argList, numArgs, optionList, numOptions, argv);

		System.out.println("Running tide-index...");

		// Build command line string
		String cmdLine = "crux tide-index";
		for (int i = 1; i < argv.length; ++i) {
			cmdLine += " ";
			cmdLine += argv[i];
		}

		
		// cmake 명령어 인듯 FLAGS_tmpfile_prefix =
		// make_file_path("modified_peptides_partial_");

		// Get Options

//		double minMass = getDoubleParameter("min-mass");
//		double maxMass = getDoubleParameter("max-mass");
//		int minLength = getIntParameter("min-length");
//		int maxLength = getIntParameter("max-length");
//		boolean monoisotopicPrecursor = getBooleanParameter("monoisotopic-precursor");
//		// FLAGSMaxMods = get_int_parameter("max-mods");
//		MASS_TYPE mass_type = (monoisotopicPrecursor) ? MONO : AVERAGE;
//		int missed_cleavages = get_int_parameter("missed-cleavages");
//		DIGEST digestion = get_digest_type_parameter("digestion");
//		ENZYME enzyme_t = get_enzyme_type_parameter("enzyme");
//		String enzyme = enzyme_type_to_string(enzyme_t);
//		
//		if(enzmye == "no-enzyme"){
//			enzyme = "none";
//		} else if (digestion != FULL_DIGEST)

		return 0;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFileStem() {
		// TODO Auto-generated method stubFLAGS_tmpfile_prefix
		return null;
	}

	@Override
	public COMMAND_T getCommand() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean needsOutputDirectory() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void initialize(String[] argumentList, int numArguments,
			String[] optionList, int numOption, String[] argv) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hidden() {
		// TODO Auto-generated method stub
		return false;
	}

}
