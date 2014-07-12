package edu.hyu.cs.jcrux;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * brief Maintains a list of executable applications.
 * 
 * @author HoonKim
 * 
 */
public class CruxApplicationList {

	/**
	 * The list of applications.
	 */
	protected LinkedList<CruxApplication> mApplications;

	/**
	 * Name of this list.
	 */
	protected String mListName;

	/**
	 * 
	 * Creates an application list with a list name.
	 * 
	 * @param listName
	 *            리스트 이름.
	 */
	public CruxApplicationList(final String listName) {
		mListName = listName;
		mApplications = new LinkedList<CruxApplication>();
	}

	/**
	 * Adds an application pointer to the list of applications.
	 * 
	 * @param application
	 *            추가할 어플리 케이션.
	 */
	public void add(final CruxApplication application) {
		if (find(application.getName()) != null) {
			Carp.carp(Carp.CARP_FATAL, "Name clash! %s", application.getName());
		}

		mApplications.addLast(application);
	}

	/**
	 * returns an application by a name. (char).
	 * 
	 * @param appName
	 *            application's name.
	 * @return application. null if not found.
	 */
	public CruxApplication find(final char[] appName) {
		return find(new String(appName));
	}

	/**
	 * returns an application by a name.
	 * 
	 * @param appName
	 *            application's name.
	 * @return application. null if not found.
	 */
	public CruxApplication find(final String appName) {
		CruxApplication cruxApplication = null;

		for (CruxApplication app : mApplications) {
			if (appName.equals(app.getName())) {
				cruxApplication = app;
				break;
			}
		}

		return cruxApplication;
	}

	/**
	 * the main method for CruxApplicationList. Attempts to find an application
	 * by name from the first argument. If successful, calls that applications
	 * main method with the rest of the parameters.
	 */
	public void usage() {
		int maxNameLength = 0;
		for (CruxApplication app : mApplications) {
			maxNameLength = (maxNameLength > app.getName().length()) ? maxNameLength
					: app.getName().length();
		}
		System.err.println("사용법을 숙지하세요!");
	}

	/**
	 * the main method for CruxApplicationList. Attempts to find an application
	 * by name from the first argument. If successful, calls that applications
	 * main method with the rest of the parameters.
	 * 
	 * @param args
	 *            CLI.
	 * @return 종료시그널.
	 */
	public int main(String args[]) {
		if (args.length < 1) {
			usage();
			return -1;
		}

		String appName = args[0];
		CruxApplication cruxApplication = find(appName);

		if (cruxApplication == null) {
			System.err.printf("Cannot fubd %s in availbale applications\n",
					appName);
			usage();
			return -1;
		}

		int ret = cruxApplication.main(args);

		return ret;
	}

}
