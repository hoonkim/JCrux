package edu.hyu.cs.jcrux.tide;

import edu.hyu.cs.pb.PeptidesPB.Peptide;

public abstract class TheoreticalPeakSet {
	public abstract void clear();

	public abstract void addYIon(double mass, int charge);

	public abstract void addBIon(double mass, int charge);

//	public abstract void getPeaks(TheroticalPeakArr peaksCharge1,
//			TheoreticalPeakArr negsCharge1, TheoreticalPeakArr peaksCharge2,
//			TheoreticalPeakArr negsCharge2, final Peptide peptide);

}
