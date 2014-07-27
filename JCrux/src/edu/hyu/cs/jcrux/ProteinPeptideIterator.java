package edu.hyu.cs.jcrux;

import edu.hyu.cs.jcrux.Objects.ENZYME;

public class ProteinPeptideIterator {
	public static boolean validCleavagePosition(final String sequence,
			ENZYME enzyme) {

		// TODO 우리는 트립신만을 고려하기로 하였으므로 트립신만 구현하였음.
		switch (enzyme) {
		case TRYPSIN:
			try {
				return ((sequence.charAt(0) == 'K' || sequence.charAt(0) == 'R') && (sequence
						.charAt(1) != 'P'));
			} catch (Exception e) {
				return false;
			}
		default:
			return false;
		}
	}
}
