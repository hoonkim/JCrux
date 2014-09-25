package edu.hyu.cs.jcrux;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale.Category;

import edu.hyu.cs.flags.Flags;
import edu.hyu.cs.jcrux.Objects.COMMAND_T;

/**
 * Abstract Object for a CruxApplication.
 * 
 * @author HoonKim
 * 
 */
public abstract class CruxApplication {

	/**
	 * the main method for CruxApplication. Subclasses of CruxApplication define
	 * this.
	 * 
	 * @param argv
	 *            CLI.
	 * @return exit code for the executed program.
	 */
	public abstract int main(final String argv[]);

	/**
	 * @return the description of the subclassed application.
	 */
	public abstract String getName();

	/**
	 * @return returns the file stem of the application, default getName.
	 */
	public String getFileStem() {
		return getName();
	}

	/**
	 * @return whether the application needs the output directory or not.
	 *         (default false).
	 */
	public abstract COMMAND_T getCommand();

	/**
	 * \returns whether the application needs the output directory or not.
	 * (default false).
	 */
	public abstract boolean needsOutputDirectory();

	/**
	 * Perform the set-up steps common to all crux commands: initialize
	 * parameters, parse command line, set verbosity, open output directory,
	 * write params file.
	 * 
	 * @param argumentList
	 *            list of required arguments
	 * @param numArguments
	 *            number of elements in arguments_list
	 * @param optionList
	 *            list of optional flags
	 * @param numOption
	 *            number of elements in options_list
	 * @param argv
	 *            array of command line tokens
	 */
	public void initialize(final String optionList[], final String argv[]) {
		Carp.setVerbosityLevel(Carp.CARP_DEBUG);

		Flags.intializeParameters(optionList, argv);
		Flags.start();
		

		Carp.carp(Carp.CARP_INFO, "Beginning %s", getName());

		if (Flags.getStringParameter("seed") != null
				&& Flags.getStringParameter("seed").equals("time")) {
			Utils.mySRandom(System.currentTimeMillis());
		} else {
			Utils.mySRandom(Flags.getIntParameter("seed"));
		}

		Utils.wallClock();

		if (needsOutputDirectory()) {
			String outputFolder = Flags.getStringParameter("output-dir");
			boolean overwrite = Flags.getBooleanParameter("overwrite");
//			boolean result = CruxUtils.createOutputDirectory(outputFolder,
//					overwrite);
//			if (result) {
//				Carp.carp(Carp.CARP_FATAL,
//						"Unable to create output directory %s.", outputFolder);
//			}

			String cmdFileName = getFileStem();

			// Open the log file to record carp messages
			String logFileName = cmdFileName + ".log.txt";
			Carp.openLogFile(logFileName);

			// Store the host name, start date and time, and command line.
			Carp.carp(Carp.CARP_INFO, "CPU: %s",
					System.getProperty("user.name"));

			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();

			Carp.carp(Carp.CARP_INFO, dateFormat.format(date));
			Carp.logCommandLine(argv);

			// // Write the parameter file
			// String paramFileName = cmdFileName + ".params.txt";

		}

	}

	/**
	 * @return Should this application be kept from the usage statement?
	 */
	public abstract boolean hidden();
}
