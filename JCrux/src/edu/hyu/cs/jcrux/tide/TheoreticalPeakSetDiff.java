package edu.hyu.cs.jcrux.tide;

public class TheoreticalPeakSetDiff extends TheoreticalPeakSet {

	private TheoreticalPeakSetBYAll byAll;
	private TheoreticalPeakSetMakeAll makeAll;

	private TheoreticalPeakArr makeAll1;
	private TheoreticalPeakArr makeAll2;
	private TheoreticalPeakArr byAll1;
	private TheoreticalPeakArr byAll2;

	public TheoreticalPeakSetDiff(int capacity) {
		byAll = new TheoreticalPeakSetBYAll(capacity);
		makeAll = new TheoreticalPeakSetMakeAll(capacity);
		makeAll1 = new TheoreticalPeakArr(capacity);
		makeAll2 = new TheoreticalPeakArr(capacity);
		byAll1 = new TheoreticalPeakArr(capacity);
		byAll2 = new TheoreticalPeakArr(capacity);
	}

	@Override
	public void clear() {
		byAll.clear();
		makeAll.clear();
		makeAll1.clear();
		makeAll2.clear();
		byAll1.clear();
		byAll2.clear();
	}

	@Override
	public void addYIon(double mass, int charge) {
		byAll.addYIon(mass, charge);
		makeAll.addYIon(mass, charge);
	}

	@Override
	public void addBIon(double mass, int charge) {
		byAll.addBIon(mass, charge);
		makeAll.addBIon(mass, charge);
	}

	@Override
	public void getPeaks(TheoreticalPeakArr peaksCharge1,
			TheoreticalPeakArr negsCharge1, TheoreticalPeakArr peaksCharge2,
			TheoreticalPeakArr negsCharge2) {

		makeAll.getPeaks(makeAll1, null, makeAll2, null);
		byAll.getPeaks(byAll1, null, byAll2, null);

		diff(makeAll1, byAll1, peaksCharge1, negsCharge1);
		diff(makeAll2, byAll2, peaksCharge2, negsCharge2);

	}

}
