package edu.hyu.cs.jcrux;

/**
 * @author HoonKim
 *
 */
/**
 * @author HoonKim
 * 
 */
/**
 * @author HoonKim
 * 
 */
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
	 * The enum for window type for selecting peptides or assigning ions.
	 * 
	 * @author HoonKim
	 * 
	 */

	public static enum WINDOW_TYPE {
		WINDOW_INVALID, WINDOW_MASS, WINDOW_MZ, WINDOW_PPM, NUMBER_WINDOW_TYPES
	}

	/**
	 * The enum for measure type for spectral counts.
	 * 
	 * @author HoonKim
	 * 
	 */
	public static enum MEASURE_TYPE {
		MEASURE_INVALID, MEASURE_RAW, MEASURE_SIN, MEASURE_NSAF, MEASURE_DNSAF, MEASURE_EMPAI, NUMBER_MEASURE_TYPES
	}

	/**
	 * The quantification level type for spectral counts.
	 * 
	 * @author HoonKim
	 * 
	 */
	public static enum QUANT_LEVEL_TYPE {
		QUANT_LEVEL_INVALID, PEPTIDE_QUANT_LEVEL, PROTEIN_QUANT_LEVEL, NUMBER_QUANT_LEVEL_TYPES
	}

	/**
	 * The enum of type of threshold to use for spectral counts
	 * 
	 * @author HoonKim
	 * 
	 */
	public static enum THRESHOLD {
		THRESHOLD_INVALID, THRESHOLD_NONE, THRESHOLD_QVALUE, THRESHOLD_CUSTOM, NUMBER_THRESHOLD_TYPES
	}

	/**
	 * The enum for parsimony type for spectral counts.
	 * 
	 * @author HoonKim
	 * 
	 */
	public static enum PARSIMONY_TYPE {
		PARSIMONY_INVALID, PARSIMONY_SIMPLE, PARSIMONY_GREEDY, PARSIMONY_NONE, NUMBER_PARSIMONY_TYPES
	}

	/**
	 * The enum for peak sort type
	 * 
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

	/**
	 * The enum for mass format.
	 * 
	 * @author HoonKim
	 * 
	 */
	public static enum MASS_FORMAT {
		INVALID_MASS_FORMAT, MOD_MASS_ONLY, AA_PLUS_MOD, MOD_MASSES_SEPARATE, NUMBER_MASS_FORMATS
	}

	/**
	 * The enum for charge state.
	 * 
	 * @author HoonKim
	 * 
	 */
	public static enum CHARGE_STATE {
		INVALID_CHARGE_STATE, SINGLE_CHARGE_STATE, MULTIPLE_CHARGE_STATE, NUMBER_CHARGE_STATE
	};

	/**
	 * The enum for sort type (mass, length, lexical, none)
	 * 
	 * @author HoonKim
	 * 
	 */
	public static enum SORT_TYPE {
		SORT_NONE, SORT_MASS, SORT_LENGTH, SORT_LEXICAL, NUMBER_SORT_TYPES
	}

	/**
	 * The enum for index type.
	 * 
	 * @author HoonKim
	 * 
	 */
	public static enum INDEX_TYPE {
		DB_INDEX, BIN_INDEX
	}

	/**
	 * The enum for an ion modification.
	 * 
	 * @author HoonKim
	 * 
	 */
	public static enum ION_TYPE {
		A_ION, B_ION, C_ION, X_ION, Y_ION, Z_ION, P_ION, BY_ION, BYA_ION, ALL_ION, NUMBER_ION_TYPES
	};

	/**
	 * 
	 * The enum for peak sort type
	 * 
	 * @author HoonKim
	 * 
	 */
	public static enum PEAK_SORT_TYPE {
		_PEtAK_LOCATION, _PEAK_INTENSITY
	};

	/**
	 * The enum for scorer type. Scores are indexed by this type in the Match.
	 * 
	 * @author HoonKim
	 * 
	 */
	public static enum SCORER_TYPE {
		SP, XCORR, EVALUE,

		DECOY_XCORR_QVALUE, DECOY_XCORR_PEPTIDE_QVALUE, DECOY_XCORR_PEP,

		DECOY_EVALUE_QVALUE, DECOY_EVALUE_PEPTIDE_QVALUE, DECOY_EVALUE_PEP,

		LOGP_WEIBULL_XCORR, LOGP_BONF_WEIBULL_XCORR, LOGP_QVALUE_WEIBULL_XCORR, LOGP_WEIBULL_PEP, LOGP_PEPTIDE_QVALUE_WEIBULL,

		PERCOLATOR_SCORE, PERCOLATOR_QVALUE, PERCOLATOR_PEPTIDE_QVALUE, PERCOLATOR_PEP,

		QRANKER_SCORE, QRANKER_QVALUE, QRANKER_PEPTIDE_QVALUE, QRANKER_PEP,

		BARISTA_SCORE, BARISTA_QVALUE, BARISTA_PEPTIDE_QVALUE, BARISTA_PEP,

		DELTA_CN, DELTA_LCN, BY_IONS_MATCHED, BY_IONS_TOTAL,

		NUMBER_SCORER_TYPES, INVALID_SCORER_TYPE
	}

	/**
	 * The enum for protein scorer type.
	 * 
	 * @author HoonKim
	 * 
	 */
	public static enum PROTEIN_SCORER_TYPE {
		PROTEIN_SCORER_PVALUE, PROTEIN_SCORER_OLIVER, NUMBER_PROTEIN_SCORER_TYPES
	}

	/**
	 * The enum for algorithm type (PERCOLATOR, CZAR, ALL).
	 * 
	 * @author HoonKim
	 * 
	 */
	public static enum ALGORITHM_TYOPE {
		PERCOLATOR_ALGORITHM, RCZAR_ALGORITHM, QVALUE_ALGORITHM, NO_ALGORITHM, ALL_ALGORITHM, QRANKER_ALGORITHM, NUMBER_ALGORITHM_TYPES
	}

	/**
	 * One value for each command that can be passed to crux (e.g.
	 * search-for-matches, sequest-search, percolator).
	 * 
	 * @author HoonKim
	 * 
	 */

	//@formatter:off
	public static enum COMMAND {
		INVALID_COMMAND, INDEX_COMMAND, SEARCH_COMMAND, SEQUEST_COMMAND, QVALUE_COMMAND, 
		GENERATE_DECOYS_COMMAND, PERCOLATOR_COMMAND, TIDE_INDEX_COMMAND, TIDE_SEARCH_COMMAND, READ_SPECTRUMRECORDS_COMMAND, 
		SPECTRAL_COUNTS_COMMAND, QRANKER_COMMAND, BARISTA_COMMAND, PROCESS_SPEC_COMMAND, 
		XLINK_SEARCH_COMMAND, GENERATE_PEPTIDES_COMMAND, GET_MS2_SPECTRUM_COMMAND,
		PREDICT_PEPTIDE_IONS_COMMAND, VERSION_COMMAND, MISC_COMMAND, NUMBER_COMMAND_TYPES
	}
	//@formatter:on

	/**
	 * Identifying which set the PSM belongs to
	 * 
	 * @author HoonKim
	 * 
	 */
	public static enum SET_TYPE {
		SET_TARGET, SET_DECOY1, SET_DECOY2, SET_DECOY3
	}

	/**
	 * An indication of where an AA_MOD may occur within a peptide.
	 * 
	 * @author HoonKim
	 * 
	 */
	public static enum MOD_POSITION {
		ANY_POSITION, C_TERM, N_TERM
	}

	/**
	 * An indication of the type of the crosslinking site that may occur in a
	 * peptide.
	 * 
	 * @author HoonKim
	 * 
	 */
	public static enum XLINK_SITE {
		XLINKSITE_UNKNOWN, XLINKSITE_NTERM, XLINKSITE_ALL, XLINKSITE_AA, NUMBER_XLINKSITES
	};

	//@formatter:off
	public static enum COMPARISON {
		COMPARISON_INVALID, COMPARISON_LT, COMPARISON_LTE,
		COMPARISON_EQ, COMPARISON_GTE, COMPARISON_GT, 
		COMPARISON_NEQ, NUMBER_COMPARISONS
	};
	//@formatter:on

	public static enum COLTYPE {
		COLTYPE_INVALID, COLTYPE_INT, COLTYPE_REAL, COLTYPE_STRING, NUMBER_COLTYPES
	};

	/**
	 * indication of which peptide in a crosslinked peptide to generate ions for
	 * 
	 * @author HoonKim
	 * 
	 */
	public static enum SPLITTYPE {
		SPLITTYPE_INVALID, SPLITTYPE_BOTH, SPLITTYPE_A, SPLITTYPE_B, NUMBER_SPLITTYPES
	};

	/**
	 * indication of which file format is read by the Barista or QRanker
	 * 
	 * @author HoonKim
	 * 
	 */
	public static enum FILE_FORMAT_T {
		INVALID_FORMAT, SQT_FORMAT, XML_FORMAT, DELIMITED_FORMAT
	};
}
