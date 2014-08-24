package edu.hyu.cs.jcrux;

import edu.hyu.cs.flags.Flags;
import edu.hyu.cs.jcrux.tide.TideIndexApplication;
import edu.hyu.cs.jcrux.tide.TideSearchApplication;

/**
 * The starting point for crux. Prints a general usage statement when given no
 * arguments. Runs one of the crux commands, including printing the current
 * version number.
 * 
 * @author HoonKim
 * 
 */
public class CruxMain {

	/**
	 * The starting point for crux. Prints a general usage statement when given
	 * no arguments. Runs one of the crux commands, including printing the
	 * current version number.
	 * 
	 * @param args
	 *            CLI.
	 */
	public static void main(String args[]) {
		CruxApplicationList applications = new CruxApplicationList("crux");

		
		applications.add(new TideIndexApplication());
		applications.add(new TideSearchApplication());
		
		
		Flags.start();
		applications.main(args);
		Flags.check();

	}
}
