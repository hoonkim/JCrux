package edu.hyu.cs.jcrux;

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
	public abstract String getFileStem();

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
	public abstract void initialize(final String argumentList[],
			final int numArguments, final String optionList[],
			final int numOption, final String argv[]);

	/**
	 * @return Should this application be kept from the usage statement?
	 */
	public abstract boolean hidden();
}
