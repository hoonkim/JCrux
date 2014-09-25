package edu.hyu.cs.jcrux.tide;

import java.util.LinkedList;

import edu.hyu.cs.jcrux.Carp;
import edu.hyu.cs.pb.PeptidesPB;
import edu.hyu.cs.pb.RawProteinsPB.Protein;

public class Peptide {

	private int mLen;
	private double mMass;
	private int mId;
	private int mFirstLocProteinId;
	private int mFirstLocPos;
	private boolean mHasAuxLocationsIndex;
	private int mAuxLocationsIndex;
	private String mResidues;
	private int mNumMods;
	private/* Mod */int mMods[];
	private boolean mDecoy;

	/* void * mProg1 */
	/* void * mProg2 */

	public Peptide(PeptidesPB.Peptide peptide, LinkedList<Protein> proteins) {
		mLen = peptide.getLength();
		mMass = peptide.getMass();
		mId = peptide.getId();
		mFirstLocProteinId = peptide.getFirstLocation().getProteinId();
		mFirstLocPos = peptide.getFirstLocation().getPos();
		mHasAuxLocationsIndex = peptide.hasAuxLocationsIndex();
		mAuxLocationsIndex = peptide.getAuxLocationsIndex();
		mMods = null;
		mNumMods = 0;
		mDecoy = peptide.getIsDecoy();
		// mProg1 = null;
		// mProg2 = null;

		// 포인터로 되있는 부분을 subString으로 대체함. 문제가 될 수 있음 나중에 체크.
		
		mResidues = proteins.get(mFirstLocProteinId).getResidues()
				.substring(mFirstLocPos);

		if (peptide.getModificationsCount() > 0) {
			mNumMods = peptide.getModificationsCount();
			mMods = new int[mNumMods];
		}
		for (int i = 0; i < mNumMods; ++i) {
			mMods[i] = peptide.getModifications(i);
		}

	}

	public String getSeq() {
		return mResidues.substring(0, mLen);
	}

	public void computeTheoreticalPeaks(TheoreticalPeakSet workSpace) {
		addIons(workSpace);
	}

	public final int getLen() {
		return mLen;
	}

	public final double getMass() {
		return mMass;
	}

	public final int getId() {
		return mId;
	}

	public final int getFirstLocProteinId() {
		return mFirstLocProteinId;
	}

	public final int getFirstLocPos() {
		return mFirstLocPos;
	}

	public final boolean isHasAuxLocationsIndex() {
		return mHasAuxLocationsIndex;
	}

	public final int getAuxLocationsIndex() {
		return mAuxLocationsIndex;
	}

	public final String getResidues() {
		return mResidues;
	}

	public final int getNumMods() {
		return mNumMods;
	}

	public final int[] getMods() {
		return mMods;
	}

	public final boolean isDecoy() {
		return mDecoy;
	}

	private void addIons(TheoreticalPeakSet workSpace) {
		double maxPossiblePeak = Double.POSITIVE_INFINITY;
		if (MaxMZ.global().getMaxBin() > 0) {
			System.out.println("Nope");
			maxPossiblePeak = MaxMZ.binInvert(MaxMZ.global().getCacheBinEnd());
		}

		LinkedList<Double> massesCharge1 = new LinkedList<Double>();
		LinkedList<Double> massesCharge2 = new LinkedList<Double>();

		for (int i = 0; i < mLen; ++i) {
			massesCharge1.add(MassConstants.AA_BIN1[mResidues.charAt(i)]);
			massesCharge2.add(MassConstants.AA_BIN2[mResidues.charAt(i)]);
		}
		

		for (int i = 0; i < mNumMods; ++i) {
			int index = 0;
			double delta;
			index = MassConstants.decodeModAAIndex(mMods[i], index);
			delta = MassConstants.decodeModDelta(mMods[i]);
			massesCharge1.set(index, massesCharge1.get(index) + delta);
			massesCharge2.set(index, massesCharge2.get(index) + delta / 2);
		}
		

		// Add all charge 1 B ions.
		double total = massesCharge1.get(0);

		for (int i = 1; (i < mLen) && (total <= maxPossiblePeak); ++i) {

			workSpace.addBIon(total, 1);

			total += massesCharge1.get(i);
		}
		

		// System.out.println(total);

		// Add all charge 2 B ions.
		total = massesCharge2.get(0);
		for (int i = 1; (i < mLen) && (total <= maxPossiblePeak); ++i) {

			workSpace.addBIon(total, 2);

			total += massesCharge2.get(i);

		}

		// Add all charge 1 Y ions.
		total = massesCharge1.get(mLen - 1);

		for (int i = mLen - 2; (i >= 0) && (total <= maxPossiblePeak); --i) {

			workSpace.addYIon(total, 1);

			total += massesCharge1.get(i);

		}

		// Add all charge 2 Y ions.
		total = massesCharge2.get(mLen - 1);

		for (int i = mLen - 2; (i >= 0) && total <= maxPossiblePeak; --i) {

			workSpace.addYIon(total, 2);

			total += massesCharge2.get(i);

		}

	}
}
