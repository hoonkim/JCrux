package edu.hyu.cs.jcrux.tide;

import edu.hyu.cs.jcrux.tide.TheoreticalPeakPair.TheoreticalPeakType;

public class TheoreticalPeakSetMakeAll extends TheoreticalPeakSet {

	OrderedPeakSets orderedPeakSets;

	public TheoreticalPeakSetMakeAll(int capacity) {
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
		orderedPeakSets.merge(null);
		// if(false /*FLAGS_dups_ok*/){
		// copyUnordered(orderedPeakSets.mTemp1, peaksCharge1);
		// copyUnordered(orderedPeakSets.mTemp3, peaksCharge2);
		// } else {
		removeDups(orderedPeakSets.mTemp1, peaksCharge1);
		removeDups(orderedPeakSets.mTemp3, peaksCharge2);
		// }

	}

	private void addYIon(double mass, int charge, TheoreticalPeakArr dest) {
		// H2O
		int index = (int) (mass + IonOffsets.Y_H2O[charge]);
		addPeak(dest, index, TheoreticalPeakType.LOSS_PEAK);
		// NH3
		index = (int) (mass + IonOffsets.Y_NH3[charge]);
		addPeak(dest, index, TheoreticalPeakType.LOSS_PEAK);
		index = (int) (mass + IonOffsets.Y[charge]);
		if (true /* FLAGS_flanks */) {
			addPeak(dest, index - 1, TheoreticalPeakType.FLANKING_PEAK);
		}
		if (true /* FLAGS_flanks */) {
			addPeak(dest, index + 1, TheoreticalPeakType.FLANKING_PEAK);
		}
	}

	private void addBIon(double mass, int charge, TheoreticalPeakArr dest) {
		// A-Ion
		int index = (int) (mass + IonOffsets.A[charge]);
		addPeak(dest, index, TheoreticalPeakType.LOSS_PEAK);
		// H2O
		index = (int) (mass + IonOffsets.B_H2O[charge]);
		addPeak(dest, index, TheoreticalPeakType.LOSS_PEAK);
		// Rest of peaksare as for Y ion.
		// NH3
		index = (int) (mass + IonOffsets.B_NH3[charge]);
		addPeak(dest, index, TheoreticalPeakType.LOSS_PEAK);
		index = (int) (mass + IonOffsets.B[charge]);
		if (true /* FLAGS_flanks */) {
			addPeak(dest, index - 1, TheoreticalPeakType.FLANKING_PEAK);
		}
		if (true /* FLAGS_flanks */) {
			addPeak(dest, index + 1, TheoreticalPeakType.FLANKING_PEAK);
		}

	}

}
