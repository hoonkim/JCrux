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
		if (outputDirectory == null) {
			outputDirectory = "crux-output";
		}

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

	public static int createOutputDirectory(final String outputFolder,
			boolean overwrite) {

		int result = -1;

		File directory = new File(outputFolder);

		if (directory.exists()) {
			if (!directory.isDirectory()) {
				Carp.carp(
						Carp.CARP_ERROR,
						"A non-directory file named '%s' already exists, \n"
								+ "so that name can't be used for an output directory \n",
						outputFolder);
				result = -1;
			} else {
				if (!overwrite) {
					Carp.carp(Carp.CARP_WARNING, "The output directory '%s' "
							+ "already exists.\nExisting files will not"
							+ " be overwritten.", outputFolder);
					result = 0;
				} else {
					Carp.carp(Carp.CARP_WARNING,
							"The output directory '%s' already exists.\nExisting files will"
									+ " be overwritten.", outputFolder);
					result = 0;
				}
			}
		} else {
			directory.mkdir();
			Carp.carp(Carp.CARP_INFO,
					"Writing result to output directory '%s'.", outputFolder);
			result = 0;
		}
		return result;
	}

	public static boolean fileExists(final String fileName) {
		return new File(fileName).exists();
	}

	public static void fileDelete(final String fileName) {
		new File(fileName).delete();
	}
}
