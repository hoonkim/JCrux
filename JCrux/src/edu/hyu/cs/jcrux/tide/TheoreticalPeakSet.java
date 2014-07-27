package edu.hyu.cs.jcrux.tide;

import java.util.ArrayList;
import java.util.LinkedList;

import com.google.protobuf.RepeatedFieldBuilder;

import edu.hyu.cs.pb.PeptidesPB.Peptide;

public abstract class TheoreticalPeakSet {

	public final static double MONO_H2O = 18.01056470;
	public final static int BIN_SHIFT_A_ION_CHG_1 = 28;
	public final static int BIN_SHIFT_A_ION_CHG_2 = 14;
	public final static int BIN_SHIFT_H2O_CHG_1 = 18;
	public final static int BIN_SHIFT_H2O_CHG_2 = 9;
	public final static int BIN_SHIFT_NH3_CHG_1 = 17;
	public final static int BIN_SHIFT_NH3_CHG_2_CASE_A = 9;
	public final static int BIN_SHIFT_NH3_CHG_2_CASE_B = 8;

	public final static int NUM_PEAK_TYPES = 10;

	public abstract void clear();

	public abstract void addYIon(double mass, int charge);

	public abstract void addBIon(double mass, int charge);

	public abstract void getPeaks(TheoreticalPeakArr peaksCharge1,
			TheoreticalPeakArr negsCharge1, TheoreticalPeakArr peaksCharge2,
			TheoreticalPeakArr negsCharge2);

	public static void addPeak(TheoreticalPeakArr dest, int index,
		TheoreticalPeakPair.TheoreticalPeakType intensity) {
		TheoreticalPeakPair peak = new TheoreticalPeakPair(index, intensity);

		dest.addLast(peak);
	}

	public static void copy(TheoreticalPeakArr src, TheoreticalPeakArr dest) {
		if (MaxMZ.global().getMaxBin() > 0) {
			int end = MaxMZ.global().getCacheBinEnd() * NUM_PEAK_TYPES;
			for (int i = 0; i < src.size() && (src.get(i).getCode() < end); i++) {
				dest.addLast(src.get(i));
			}
		} else {
			for (int i = 0; i < src.size(); i++) {
				dest.addLast(src.get(i));
			}
		}
	}

	public static void copyUnordered(TheoreticalPeakArr src,
			TheoreticalPeakArr dest) {
		if (MaxMZ.global().getMaxBin() > 0) {
			int end = MaxMZ.global().getCacheBinEnd() * NUM_PEAK_TYPES;
			for (int i = 0; i < src.size(); i++) {
				if (src.get(i).getCode() < end) {
					dest.addLast(src.get(i));
				}
			}
		} else {
			for (int i = 0; i < src.size(); i++) {
				dest.addLast(src.get(i));
			}
		}
	}

	protected static void removeDups(TheoreticalPeakArr src,
			TheoreticalPeakArr dest) {
		for (int i = 0; i < src.size(); i++) {
			int index = src.get(i).getBin();
			if (MaxMZ.global().getMaxBin() > 0
					&& index >= MaxMZ.global().getCacheBinEnd()) {
				break;
			}
			for (++i; (i != src.size()) && (src.get(i).getBin() == index); ++i) {
				dest.addLast(src.get(i));
			}
		}
	}

	public static void diff(TheoreticalPeakArr x, TheoreticalPeakArr y,
			TheoreticalPeakArr pos, TheoreticalPeakArr neg) {
		int xc = 0;
		int yc = 0;
		while ((xc < x.size()) && (yc < y.size())) {
			System.out.println("xc :" + xc + ", yc: " + yc);

			if (x.get(xc).isSmaller(y.get(yc))) {
				pos.addLast(x.get(xc++));
			} else if (x.get(xc).isBigger(y.get(yc))) {
				neg.addLast(y.get(yc++));
			} else {
				xc++;
				yc++;
			}

		}

		for (; xc < x.size(); xc++) {
			pos.addLast(x.get(xc));
		}

		for (; yc < y.size(); yc++) {
			neg.addLast(y.get(yc));
		}

	}

}
