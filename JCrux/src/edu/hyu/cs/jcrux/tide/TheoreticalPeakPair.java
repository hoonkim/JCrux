package edu.hyu.cs.jcrux.tide;

public class TheoreticalPeakPair {

	enum TheoreticalPeakType {
		PEAK_MAIN(0), LOSS_PEAK(1), FLANKING_PEAK(2), PRIMARY_PEAK(3), PEAK_COMBINED_B1(
				4), PEAK_COMBINED_Y1(5), PEAK_COMBINED_B2A(6), PEAK_COMBINED_Y2A(
				7), PEAK_COMBINED_B2B(8), PEAK_COMBINED_Y2B(9), NUM_PEAK_TYPES(
				10);

		private int mValue;

		private TheoreticalPeakType(int value) {
			mValue = value;
		}

		public int getValue() {
			return mValue;
		}
	}

	final private int NUM_PEAK_TYPES = 10;

	private int mCode;

	private TheoreticalPeakPair() {
		return;
	}

	public TheoreticalPeakPair(int code) {
		this();
		mCode = code;
	}

	public TheoreticalPeakPair(int bin, TheoreticalPeakType peakType) {
		mCode = bin * NUM_PEAK_TYPES + peakType.getValue();
	}

	int getBin(){
		return mCode /NUM_PEAK_TYPES;
	}
	
	int getType() {
		return mCode % NUM_PEAK_TYPES;
	}
	
	int getCode() {
		return mCode;
	}
	
	boolean isBigger(TheoreticalPeakPair other){
		return mCode > other.getCode();
	}
	
	boolean isSmaller(TheoreticalPeakPair other){
		return mCode < other.getCode();
	}
}
