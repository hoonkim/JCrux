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
}
