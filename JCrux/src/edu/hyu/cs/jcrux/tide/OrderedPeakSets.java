package edu.hyu.cs.jcrux.tide;

import java.util.List;

import edu.hyu.cs.pb.PeptidesPB.Peptide;

public class OrderedPeakSets {
	private static final int NUM_PEAK_TYPES = 10;
	TheoreticalPeakArr mBSeries[] = new TheoreticalPeakArr[2];
	TheoreticalPeakArr mYSeries[] = new TheoreticalPeakArr[2];
	TheoreticalPeakArr mTemp1, mTemp2, mTemp3;

	public OrderedPeakSets(int capacity) {
		mBSeries[0] = new TheoreticalPeakArr(capacity);
		mBSeries[1] = new TheoreticalPeakArr(capacity);
		mYSeries[0] = new TheoreticalPeakArr(capacity);
		mYSeries[1] = new TheoreticalPeakArr(capacity);
		mTemp1 = new TheoreticalPeakArr(capacity);
		mTemp2 = new TheoreticalPeakArr(capacity);
		mTemp3 = new TheoreticalPeakArr(capacity);
	}

	public void clear() {
		mBSeries[0].clear();
		mBSeries[1].clear();
		mYSeries[0].clear();
		mYSeries[1].clear();
	}

	public void merge(Peptide peptide) {
		if (peptide != null && peptide.getPeak1Count() > 0) {
			mergePeaks(mBSeries[0], mYSeries[0], mTemp3);
			mergeExceptions(mTemp3, peptide.getPeak1List(), mTemp1);
		} else {
			mergePeaks(mBSeries[0], mYSeries[1], mTemp1);
		}
		if ((peptide != null) && (peptide.getPeak2Count() > 0)) {
			mergePeaks(mBSeries[1], mYSeries[1], mTemp3);
			mergeExceptions(mTemp3, peptide.getPeak2List(), mTemp2);
		} else {
			mergePeaks(mBSeries[1], mYSeries[1], mTemp2);
		}
		mergePeaks(mTemp1, mTemp2, mTemp3);
	}

	private void mergePeaks(TheoreticalPeakArr a, TheoreticalPeakArr b,
			TheoreticalPeakArr result) {
		result.clear();
		for (int i = 0; i < a.size(); i++) {
			result.addLast(a.get(i));
		}
		for (int i = 0; i < b.size(); i++) {
			result.addLast(b.get(i));
		}
	}

	private void mergeExceptions(TheoreticalPeakArr src, List<Integer> exc,
			TheoreticalPeakArr dest) {
		dest.clear();
		// The protocol buffer stores deltas between peak values, so we need
		// to track the total:
		int total = 0;
		int end = MaxMZ.global().getCacheBinEnd() * NUM_PEAK_TYPES;

		int excIter = 0;
		int srcIter = 0;

		if ((exc.size() != 0) && (src.size() != 0)) {
			while (true) {
				boolean advanceSrc = total >= src.get(srcIter).getCode();
				boolean advanceExc = total <= src.get(srcIter).getCode();
				if (advanceSrc) {
					dest.addLast(src.get(srcIter++));
					if ((srcIter == src.size())
							|| (src.get(srcIter).getCode() >= end)) {
						break;
					}
				}
				if (advanceExc) {
					dest.addLast(new TheoreticalPeakPair(total));
					total += ++excIter;
					if ((excIter == exc.size()) || (total >= end)) {
						break;
					}
				}
			}
		}

		if (total < end) {
			for (; excIter != exc.size(); ++excIter) {
				if ((total += excIter) >= end) {
					break;
				}
				dest.addLast(new TheoreticalPeakPair(total));
			}
		}
		for (; srcIter != src.size() && (src.get(srcIter).getCode() < end); ++srcIter) {
			dest.addLast(src.get(srcIter));
		}
	}

}
