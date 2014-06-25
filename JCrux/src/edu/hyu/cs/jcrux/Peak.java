package edu.hyu.cs.jcrux;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import edu.hyu.cs.jcrux.Objects.PEAK_SORT_TYPE;

/**
 * Object for representing one peak in a spectrum
 * 
 * A peak is primarily identified via its intensity (height) and location
 * (position on the m/z axis)
 * 
 * @author HoonKim
 * 
 */
public class Peak {

	/**
	 * The intensity of this peak
	 */
	private double mIntensity;

	/**
	 * The rank intensity of this peak
	 */
	private double mIntensityRank;

	/**
	 * The location of this peak
	 */
	private double mLocation;

	public Peak(double intensity, double location) {
		mIntensity = intensity;
		mIntensityRank = 0.0;
		mLocation = location;
	}

	public double getIntensity() {
		return mIntensity;

	}

	public double getIntensityRank() {
		return mIntensityRank;
	}

	public double getLocation() {
		return mLocation;
	}

	/**
	 * Set the intensity of this Peak
	 */
	public void setIntensity(double intensity) {
		mIntensity = intensity;
	}

	/**
	 * Set the intensity rank of this Peak
	 */
	public void setIntensityRank(double intensityRank) {
		mIntensityRank = intensityRank;
	}

	/**
	 * Set the location of this Peak
	 */
	public void setLocation(double location) {
		mLocation = location;
	}

	/**
	 * Print the intensity and location of this peak to stdout
	 */
	void print() {
		System.out.printf("%.1f %.1f\n", mLocation, mIntensity);
	}

	/**
	 * Compare the intensity of this Peak and another Peak Return true if this
	 * Peak is greater, false otherwise
	 */
	boolean compareByIntensity(Peak other) {
		// 내림차순.
		return getIntensity() > other.getIntensity();
	}

	/**
	 * Compare the mz(location) of this Peak and another Peak Return true if the
	 * other Peak is greater, false otherwise
	 */
	boolean compareByMZ(Peak other) {
		// 오름차순.
		return getLocation() < other.getLocation();
	}

	/**
	 * return a vector of allocated Peak pointers
	 */
	public static LinkedList<Peak> allocatePeakVector(int numPeaks) {
		LinkedList<Peak> ans = new LinkedList<Peak>();
		for (int idx = 0; idx < numPeaks; idx++) {
			ans.addLast(new Peak(0, 0));
		}
		return ans;
	}

	public static void freePeakVector(LinkedList<Peak> peaks) {
		peaks.clear();
	}

	public static void sortPeaks(LinkedList<Peak> peakArray,
			PEAK_SORT_TYPE sortType) {
		if (sortType == PEAK_SORT_TYPE._PEAK_INTENSITY) {
			Collections.sort(peakArray, new CompareByIntensity());
		} else if (sortType == PEAK_SORT_TYPE._PEtAK_LOCATION) {
			Collections.sort(peakArray, new CompareByMz());
		}
	}

	public static class CompareByIntensity implements Comparator<Peak> {

		@Override
		public int compare(Peak arg0, Peak arg1) {
			if (arg0.compareByIntensity(arg1)) {
				return -1;
			} else {
				return 1;
			}
		}

	}

	public static class CompareByMz implements Comparator<Peak> {

		@Override
		public int compare(Peak arg0, Peak arg1) {
			if (arg0.compareByMZ(arg1)) {
				return -1;
			} else {
				return 1;
			}
		}

	}
}
