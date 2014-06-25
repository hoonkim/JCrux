package edu.hyu.cs.jcrux.tide;

public class ModCoder {

	private int mLogUniqueDeltas;
	private int mMask;

	public void init(int numUniqueDeltas) {
		mLogUniqueDeltas = intLog(numUniqueDeltas);
		mMask = (1 << mLogUniqueDeltas) - 1;
	}

	public int encodeMod(int aaIndex, int uniqueDeltaIndex) {
		return (aaIndex << mLogUniqueDeltas) + uniqueDeltaIndex;
	}

	public int decodeModAAIndex(int code) {
		return code >> mLogUniqueDeltas;
	}

	public int decodeModUniqueDeltaIndex(int code) {
		return code & mMask;
	}

	private static int intLog(int x) {
		if (x <= 1) {
			return x;
		}
		int res = 0;
		for (--x; x > 0; x >>= 1) {
			++res;
		}
		return res;
	}

}
