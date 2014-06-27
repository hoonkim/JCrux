package edu.hyu.cs.jcrux;

import java.io.File;

/**
 * Provides methods for logging error messages, and setting verbosity level.
 * 
 * @author HoonKim
 * 
 */
public class Carp {

	/**
	 * Verbosity level for a fatal error (e.g., could not open an input file).
	 */
	public static final int CARP_FATAL = 0;

	/**
	 * Verbosity level for a serious, not fatal, error, (e.g., could not close a
	 * file handle)
	 */
	public static final int CARP_ERROR = 10;

	/**
	 * Verbosity level for a warning (e.g., a spectrum has no peaks).
	 */
	public static final int CARP_WARNING = 20;

	/**
	 * Verbosity level for informational message (e.g., processed X lines of
	 * file).
	 */
	public static final int CARP_INFO = 30;

	/**
	 * Verbosity level for detailed informational message (e.g, on spectrum 1000
	 * ).
	 */
	public static final int CARP_DETAILED_INFO = 40;

	/**
	 * Verbosity level for a debugging message.
	 */
	public static final int CARP_DEBUG = 50;

	/**
	 * Verbosity level for very detailed debugging message.
	 */
	public static final int CARP_DETAILED_DEBUG = 60;

	/**
	 * The maximum verbosity level.
	 */
	public static final int CARP_MAX = 100;

	private static int mVerbosity;

	private static File logFile = null;

	public static void setVerbosityLevel(int verbosity) {
		mVerbosity = verbosity;
	}

	/**
	 * @return the current verbosity level.
	 */
	public static int getVerbosityLevel() {
		return mVerbosity;
	}

	/**
	 * Open log file for carp messages.
	 * 
	 * Parameters must have been processed before calling this function.
	 */
	public static void openLogFile(String logFileName[]) {
		
	}

	/**
	 * Print command line to log file.
	 * 
	 * Parameters must have been processed before calling this function.
	 * 
	 * @param argv
	 */
	public static void logCommandLine(String argv[]) {

	}

	/**
	 * Print message to log file.
	 * 
	 * Print severity level and message to log file. The term 'carp' is used
	 * because 'log' is already used by the math library.
	 * 
	 * Verbosity of CARP_FATAL will cause the program to exit with status code
	 * 1.
	 * 
	 */
	public static void carp(int verbosity, final String... format) {
		// TODO 구현.
	}

	public static void warnOnce(final String msg1, final String... msgs) {

	}

	/**
	 * Print message to log file, just once.// not in java
	 */
	public static void carpOnce(int verbosity, final String... msgs) {

		carp(verbosity, msgs);
	}

}
