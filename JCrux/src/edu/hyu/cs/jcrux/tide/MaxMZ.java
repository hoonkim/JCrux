package edu.hyu.cs.jcrux.tide;

import edu.hyu.cs.flags.Flags;

public class MaxMZ {

	public MaxMZ() {
		mMaxBin = 0;
		mBackgroundBinEnd = 0;
		mCacheBinEnd = 0;
	}

	public static final double BIN_WIDTH_MONO = 1.0005079;

	public static final int MAX_XCORR_OFFSET = 75;

	public static int getBin(double mz) {
		return (int) (mz / BIN_WIDTH_MONO + 0.5);
	}

	public static double binInvert(int bin) {
		return ((double) bin + 0.505) * BIN_WIDTH_MONO;
	}

	void init(double highestMz) {
		initBin(getBin(highestMz));
	}

	void initBin(int highestBin) {
		mMaxBin = highestBin;
		mBackgroundBinEnd = mMaxBin + MAX_XCORR_OFFSET + 1;
		mCacheBinEnd = mBackgroundBinEnd + 29;
	}

	int getMaxBin() {
		return mMaxBin;
	}

	int getBackgroundBinEnd() {
		return mBackgroundBinEnd;
	}

	int getCacheBinEnd() {
		return mCacheBinEnd;
	}

	public static MaxMZ global() {
		return mGlobal;
	}

	public static void setGlobalMax(double higestMz) {
		Flags.MaxMz = higestMz;
		mGlobal.init(higestMz);
	}

	// FIXME 대체 뭔..?
	public static void setGlobalMaxFromFlag() {
		if (Flags.MaxMz > 0) {
			setGlobalMax(Flags.MaxMz);
		}
	}

	private static MaxMZ mGlobal = new MaxMZ();

	private int mMaxBin;
	private int mBackgroundBinEnd;
	private int mCacheBinEnd;

}
