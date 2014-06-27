package edu.hyu.cs.jcrux;

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
	protected LinkedList<CruxApplication> applications;

	/**
	 * Name of this list.
	 */
	protected String listName;

	/**
	 * 
	 * Creates an application list with a list name.
	 * 
	 * @param listName
	 *            리스트 이름.
	 */
	public CruxApplicationList(final String listName) {
		// @Todo 구현.
	}

	/**
	 * Adds an application pointer to the list of applications.
	 * 
	 * @param application
	 *            추가할 어플리 케이션.
	 */
	public void add(final CruxApplication application) {
		// @Todo 구현.
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
		// @Todo 구현.
		return null;
	}

	/**
	 * the main method for CruxApplicationList. Attempts to find an application
	 * by name from the first argument. If successful, calls that applications
	 * main method with the rest of the parameters.
	 */
	public void usage() {
		// @Todo 구현.
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
		// @Todo 구현.
		return 0;
	}

}
