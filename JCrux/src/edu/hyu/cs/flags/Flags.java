package edu.hyu.cs.flags;

import java.util.Locale;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import edu.hyu.cs.jcrux.Objects.DECOY_TYPE;
import edu.hyu.cs.jcrux.Objects.DIGEST;
import edu.hyu.cs.jcrux.Objects.ENZYME;

public class Flags {

	private static Options options;
	private static CommandLine line;
	public static int MaxMods;
	public static String tmpFilePrefix;

	public static void intializeParameters(final String optionList[],
			String argv[]) {
		options = new Options();
		for (String option : optionList) {
			options.addOption(null, option, true, null);
		}
		CommandLineParser parser = new PosixParser();

		try {
			line = parser.parse(options, argv);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public static String getStringParameter(final String option) {
		if (line.hasOption(option)) {
			return line.getOptionValue(option);
		} else {
			return null;
		}
	}

	public static int getIntParameter(final String option) {
		if (line.hasOption(option)) {
			return Integer.parseInt(line.getOptionValue(option));
		} else {
			return 0;
		}
	}

	public static double getDoubleParameter(final String option) {
		if (line.hasOption(option)) {
			return Double.parseDouble(line.getOptionValue(option));
		} else {
			return 0;
		}
	}

	public static DIGEST getDigestParameter(final String option) {
		// 여긴 아직 기본 Digest만 리턴하게 해놓았음.ㄴ

		return DIGEST.FULL_DIGEST;
	}

	public static ENZYME getEnzymeParameter(final String option) {
		// 여긴 아직 기본 Enzyme만 리턴하게 해놓았음.
		return ENZYME.TRYPSIN;
	}

	public static boolean getBooleanParameter(final String option) {
		if (line.hasOption(option)) {
			String bool = line.getOptionValue(option);
			// T면 True return 아니면 false return;
			return (bool.toUpperCase(Locale.US).equals("T")) ? true : false;
		}
		return false;
	}

	public static DECOY_TYPE getTideDecoyTypeParameter(final String option) {
		// TODO 여기도 기본 셔플 디코이.
		return DECOY_TYPE.PEPTIDE_SHUFFLE_DECOYS;

	}
}
