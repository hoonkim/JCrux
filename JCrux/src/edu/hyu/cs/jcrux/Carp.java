package edu.hyu.cs.jcrux;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.cli.Options;

import edu.hyu.cs.flags.Flags;

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

	private static FileWriter writer = null;

	private static boolean overwrite = false;

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
	public static void openLogFile(String logFileName) {
		Options options = new Options();
		options.addOption(null, "output-dir", true, null);
		options.addOption(null, "overwrite", true, null);

		String outputDir = Flags.getStringParameter("output-dir");
		if (outputDir == null) {
			outputDir = "crux-output/";
		}
		overwrite = Flags.getBooleanParameter(outputDir);

		if (!outputDir.endsWith("/")) {
			outputDir += "/";
		}

		CruxUtils.createOutputDirectory(outputDir, true);

		logFile = new File(outputDir + logFileName);
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Print command line to log file.
	 * 
	 * Parameters must have been processed before calling this function.
	 * 
	 * @param argv
	 */
	public static void logCommandLine(String argv[]) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(logFile,
					true);
			for (int i = 0; i < argv.length; i++) {
				String buffer = argv[i]
						+ ((i < (argv.length - 1)) ? " " : "\n");
				fileOutputStream.write(buffer.getBytes());
				fileOutputStream.flush();
			}
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			System.err.println("Something wrong file not exists");
		} catch (IOException e) {
			System.err.println("Error while writing to log file.");
		}
	}

	public static void carpPrint(final String string) {
		try {

			FileOutputStream fileOutputStream = new FileOutputStream(logFile,
					true);
			if (logFile != null) {
				fileOutputStream.write(string.getBytes());
				fileOutputStream.flush();
			}
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			System.err.println("Something wrong file not exists");
		} catch (IOException e) {
			System.err.println("Error while writing to log file.");
		}
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
	public static void carp(int verbosity, final String str,
			final Object... format) {

		if (verbosity <= mVerbosity) {
			String formattedString = String.format(str, format);
			formattedString+= '\n';

			System.err.println(formattedString);
			if (logFile != null) {
				try {
					FileOutputStream fileOutputStream = new FileOutputStream(
							logFile, true);
					if (verbosity == CARP_WARNING) {
						carpPrint("WARNING: ");
					} else if (verbosity == CARP_ERROR) {
						carpPrint("ERROR: ");
					} else if (verbosity == CARP_FATAL) {
						carpPrint("FATAL: ");
					} else if (verbosity == CARP_INFO) {
						carpPrint("INFO: ");
					} else if (verbosity == CARP_DETAILED_INFO) {
						carpPrint("DETAILED INFO: ");
					} else if (verbosity == CARP_DEBUG) {
						carpPrint("DEBUG: ");
					} else if (verbosity == CARP_DETAILED_DEBUG) {
						carpPrint("DETAILED DEBUG: ");
					} else {
						carpPrint("UNKNOWN: ");
					}

					fileOutputStream.write(formattedString.getBytes());
					fileOutputStream.flush();

					fileOutputStream.close();
					if (verbosity == CARP_FATAL) {
						System.exit(1);
					}

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		}
	}

	public static void warnOnce(final String msg1, final String... msgs) {

	}

	/**
	 * Print message to log file, just once.// not in java
	 */
	public static void carpOnce(int verbosity, final String msg,
			final Object... format) {
		carp(verbosity, msg, format);
	}

}
