package edu.hyu.cs.jcrux;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import edu.hyu.cs.flags.Flags;

public class CruxUtils {
	public static String getFullFilename(final String path,
			final String fileName) {
		String result = null;
		if (path == null) {
			result = fileName;
		} else {
			result = path + "/" + fileName;
		}
		return result;
	}

	public static File createFileInPath(final String fileName,
			final String directory, boolean overwrite) {
		String fileFullPath = getFullFilename(directory, fileName);
		File file = new File(fileFullPath);
		return file;
	}

	public static FileOutputStream createStreamInPath(final String fileName,
			final String directory, boolean overwrite) {
		String fileFullPath = getFullFilename(directory, fileName);
		File file = new File(fileFullPath);
		if (file.exists()) {
			if (!overwrite) {
				Carp.carp(Carp.CARP_FATAL, "The file '%s' already exists "
						+ "and cannot be overwritten. Use --overwrite T "
						+ "to replace or choose a different output file name",
						fileFullPath);
				return null;
			} else {
				Carp.carp(Carp.CARP_WARNING, "The file"
						+ " '%s' already exists and will be overwritten.",
						fileFullPath);
			}
		}
		try {
			FileOutputStream fout = new FileOutputStream(file);
			return fout;

		} catch (FileNotFoundException e) {
			Carp.carp(Carp.CARP_FATAL, "Failed to create and open file : %s",
					fileFullPath);
			return null;
		}

	}

	public static String makeFilePath(final String fileName) {

		String outputDirectory = Flags.getStringParameter("output-dir");
		String fileroot = Flags.getStringParameter("fileroot");

		String result = "";
		result += outputDirectory;

		if (!outputDirectory.endsWith("/")) {
			result += "/";
		}

		if (fileroot != null) {
			result = result + fileroot + ".";
		}
		result += fileName;
		return result;

	}
	
	public static boolean createOutputDirectory(final String fileName){
		return false;
	}
}
