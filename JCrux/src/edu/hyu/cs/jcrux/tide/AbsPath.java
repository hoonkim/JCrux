package edu.hyu.cs.jcrux.tide;

public class AbsPath {
	public static String absPath(final String path) {
		if (path.isEmpty()) {
			return "";
		}
		if (path.startsWith("/")) {
			return path;
		}

		// get current directory
		String cwd = System.getProperty("user.dir");

		return cwd + "/" + path;
	}
}
