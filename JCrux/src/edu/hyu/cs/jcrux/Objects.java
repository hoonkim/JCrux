package edu.hyu.cs.jcrux;

public class Objects {

	// @TODO 각종 매크로랑 #define된거나 Constant들 적당히 바꿔야됨.
	/**
	 * 
	 * One value for each command that can be passed to crux
	 * 
	 * @author HoonKim
	 * 
	 */
	public static enum COMMAND_T {
		INVALID_COMMAND, // /< required by coding standards
		INDEX_COMMAND, // /< create-index
		SEARCH_COMMAND, // /< search-for-matches
		SEQUEST_COMMAND, // /< sequest-search
		QVALUE_COMMAND, // /< compute-q-values
		GENERATE_DECOYS_COMMAND, // /< generate-decoys
		PERCOLATOR_COMMAND, // /< percolator
		TIDE_INDEX_COMMAND, // /< tide-index
		TIDE_SEARCH_COMMAND, // /< tide-search
		READ_SPECTRUMRECORDS_COMMAND, // /< read-spectrumrecords
		SPECTRAL_COUNTS_COMMAND, // /< spectral counts
		QRANKER_COMMAND, // /< q-ranker
		BARISTA_COMMAND, // /< barista
		PROCESS_SPEC_COMMAND, // /< print-processed-spectra
		XLINK_SEARCH_COMMAND, // /< search-for-xlinks
		GENERATE_PEPTIDES_COMMAND, // /< generate-peptides
		GET_MS2_SPECTRUM_COMMAND, // /<get-ms2-spectrum
		PREDICT_PEPTIDE_IONS_COMMAND, // /< predict-peptide-ions
		VERSION_COMMAND, // /< just print the version number
		MISC_COMMAND, // /< miscellaneous command
		NUMBER_COMMAND_TYPES // /< always keep this last so the value
								// / changes as cmds are added
	};

	/**
	 * The rule governing how a peptide was cleaved from its source protein
	 * sequence.
	 * 
	 * @author HoonKim
	 * 
	 */
	public static enum DIGEST {
		INVALID_DIGEST, // /< required invalid value for the enum
		FULL_DIGEST, // /< c- AND n-term specific to ENZYME_T
		PARTIAL_DIGEST, // /< c- OR n-term specific to ENZYME_T
		NON_SPECIFIC_DIGEST, // /< not specific to any enzyme cleavage rules
		NUMBER_DIGEST_TYPES // /< keep last, number of types

	}

	/**
	 * enzyme with which a peptide was digested. Used in conjunction with
	 * DIGEST_T to define how peptides are generated
	 * 
	 * @author HoonKim
	 * 
	 */
	public static enum ENZYME {
		INVALID_ENZYME, // /< required invalid value for the enum
		NO_ENZYME, // /< cleave anywhere
		TRYPSIN, // /< cleave after K or R, not before P
		TRYPSINP, // /< cleave after K or R
		CHYMOTRYPSIN, // /< cleave after FWYL, not before P
		ELASTASE, // /< cleave after ALIV, not before P
		CLOSTRIPAIN, // /< cleave after R
		CYANOGEN_BROMIDE, // /< cleave after M
		IODOSOBENZOATE, // /< cleave after W
		PROLINE_ENDOPEPTIDASE, // /< cleave after P
		STAPH_PROTEASE, // /< cleave after E
		ASPN, // /< cleave before D
		LYSC, // /< cleave after K , not befor P
		LYSN, // /< cleave before K
		ARGC, // /< cleave after R, not before P
		GLUC, // /< cleave after D or E, not before P
		PEPSINA, // /< cleave after FL, not before P
		ELASTASE_TRYPSIN_CHYMOTRYPSIN, // /< cleave after ALIVKRWFY, not before
										// P
		CUSTOM_ENZYME, // /< cleave after/before user-defined residues
		NUMBER_ENZYME_TYPES // /< leave last, number of types
	}

	/**
	 * The enum for isotopic mass type (average, mono)
	 * 
	 * @author HoonKim
	 * 
	 */
	public static enum MASS_TYPE {
		AVERAGE, MONO, NUMBER_MASS_TYPES
	}

	/**
	 * DECOY_TYPE_T
	 * 
	 * @author HoonKim
	 * 
	 */
	public static enum DECOY_TYPE {
		INVALID_DECOY_TYPE, //
		NO_DECOYS, //
		PROTEIN_REVERSE_DECOYS, //
		PROTEIN_SHUFFLE_DECOYS, //
		PEPTIDE_SHUFFLE_DECOYS, //
		PEPTIDE_REVERSE_DECOYS, //
		NUMBER_DECOY_TYPES //
	}
}
