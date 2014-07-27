package edu.hyu.cs.jcrux.tide;

import edu.hyu.cs.jcrux.tide.TheoreticalPeakPair.TheoreticalPeakType;

public class TheoreticalPeakSetBYAll extends TheoreticalPeakSet {

	private OrderedPeakSets orderedPeakSets;

	public TheoreticalPeakSetBYAll(int capacity) {
		orderedPeakSets = new OrderedPeakSets(capacity);
	}

	@Override
	public void clear() {
		orderedPeakSets.clear();
	}

	@Override
	public void addYIon(double mass, int charge) {
		addYIon(mass, charge, orderedPeakSets.mYSeries[charge - 1]);
	}

	@Override
	public void addBIon(double mass, int charge) {
		addBIon(mass, charge, orderedPeakSets.mBSeries[charge - 1]);
	}

	@Override
	public void getPeaks(TheoreticalPeakArr peaksCharge1,
			TheoreticalPeakArr negsCharge1, TheoreticalPeakArr peaksCharge2,
			TheoreticalPeakArr negsCharge2) {
		copy(orderedPeakSets.mTemp1, peaksCharge1);
		copy(orderedPeakSets.mTemp3, peaksCharge2);
	}

	private void addYIon(double mass, int charge, TheoreticalPeakArr dest) {
		int indexY = (int) (mass + IonOffsets.Y[charge]);
		if (charge == 1) {
			addPeak(dest, indexY - BIN_SHIFT_H2O_CHG_1,
					TheoreticalPeakType.LOSS_PEAK);
			addPeak(dest, indexY - BIN_SHIFT_NH3_CHG_1,
					TheoreticalPeakType.LOSS_PEAK);
		} else {
			addPeak(dest, indexY - BIN_SHIFT_H2O_CHG_2,
					TheoreticalPeakType.LOSS_PEAK);
			//@formatter:off
			if ((indexY - (int) (mass + IonOffsets.Y_NH3[charge])) 
					== BIN_SHIFT_NH3_CHG_2_CASE_B) {
			//@formatter:on
				addPeak(dest, indexY - BIN_SHIFT_NH3_CHG_2_CASE_B,
						TheoreticalPeakType.LOSS_PEAK);
			}
		}
		addPeak(dest, indexY - 1, TheoreticalPeakType.FLANKING_PEAK);
		addPeak(dest, indexY, TheoreticalPeakType.PRIMARY_PEAK);
		addPeak(dest, indexY + 1, TheoreticalPeakType.FLANKING_PEAK);
	}

	private void addBIon(double mass, int charge, TheoreticalPeakArr dest) {
		int indexB = (int) (mass + IonOffsets.B[charge]);
		if (charge == 1) {
			addPeak(dest, indexB - BIN_SHIFT_A_ION_CHG_1,
					TheoreticalPeakType.LOSS_PEAK);
			addPeak(dest, indexB - BIN_SHIFT_H2O_CHG_1,
					TheoreticalPeakType.LOSS_PEAK);
			addPeak(dest, indexB - BIN_SHIFT_NH3_CHG_1,
					TheoreticalPeakType.LOSS_PEAK);
		} else {
			addPeak(dest, indexB - BIN_SHIFT_A_ION_CHG_2,
					TheoreticalPeakType.LOSS_PEAK);
			addPeak(dest, indexB - BIN_SHIFT_H2O_CHG_2,
					TheoreticalPeakType.LOSS_PEAK);
			//@formatter:off
			if ((indexB - (int) (mass + IonOffsets.B_NH3[charge])) 
					== BIN_SHIFT_NH3_CHG_2_CASE_B) {
			//@formatter:on
				addPeak(dest, indexB - BIN_SHIFT_NH3_CHG_2_CASE_B,
						TheoreticalPeakType.LOSS_PEAK);
			}
		}
		addPeak(dest, indexB - 1, TheoreticalPeakType.FLANKING_PEAK);
		addPeak(dest, indexB, TheoreticalPeakType.PRIMARY_PEAK);
		addPeak(dest, indexB + 1, TheoreticalPeakType.FLANKING_PEAK);
	}
}
