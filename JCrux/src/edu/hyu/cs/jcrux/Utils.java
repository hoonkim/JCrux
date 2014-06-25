package edu.hyu.cs.jcrux;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Utils {
	// typedef class extends ...

	public static final int INVALID_VERBOSE = 0;
	public static final int QUIET_VERBOSE = 1;
	public static final int NORMAL_VERBOSE = 2;
	public static final int HIGH_VERBOSE = 3;
	public static final int HIGHER_VERBOSE = 4;
	public static final int DUMP_VERBOSE = 5;

	public static final int FILENAME_LENGTH = 4096;
	public static final int BAD_SCORE = -1;
	public static final int IDLENGTH = 256;
	public static final int PEPTIDELENGTH = 80;
	public static final int LINELENGTH = 4096;

	public static int verbosity;

	public static final int UNIFORM_INT_DISTRIBUTION_MAX = 2147483647;

	public static int getline(String lineptr, Integer n, File stream) {
		final int BUFFSIZE = 100;

		if (lineptr == null || !stream.canExecute() || stream.canRead()) {
			Errno.errno = Errno.EINVAL;
			return -1;
		}

		int index = 0;
		try {
			FileInputStream fis = new FileInputStream(stream);
			int c = fis.read();

			if (lineptr == null) {
				n += BUFFSIZE;
			}
			StringBuilder sb = new StringBuilder(lineptr);
			while (c != '\n') {
				sb.setCharAt(index++, (char) c);

				if (index > n - 1) {
					n += BUFFSIZE;
				}

				c = fis.read();
			}

			if (fis.available() > 0) {
				sb.setCharAt(index++, (char) c);

				if (index > n - 1) {
					n += 1;
				}

				sb.setCharAt(index, '\0');
				lineptr = sb.toString();

				return index;
			} else {
				return -1;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public static double NaN() {
		return Double.NaN;
	}

	private static boolean firstCall = true;
	private static double firstTime = 0;

	public static double wallClock() {
		double ct = System.currentTimeMillis();

		if (firstCall == true) {
			firstTime = ct;
			firstCall = false;
		} else {
			return ct - firstTime;
		}
		return ct;

	}
}
