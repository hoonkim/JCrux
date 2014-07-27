package edu.hyu.cs.jcrux.tide;

public class IonOffsets {
	public static double A[] = { 0, ionOffset(-28, 1), ionOffset(-28, 2) };
	public static double B_H2O[] = { 0, ionOffset(-18, 1), ionOffset(-18, 2) };
	public static double B_NH3[] = { 0, ionOffset(-17, 1), ionOffset(-17, 2) };
	public static double B[] = { 0, ionOffset(0, 1), ionOffset(0, 2) };
	public static double MONO_H2O = 18.01056470;
	public static double Y_H2O[] = { 0, ionOffset(MONO_H2O - 18, 1),
			ionOffset(MONO_H2O - 18, 2) };
	public static double Y_NH3[] = { 0, ionOffset(MONO_H2O - 17, 1),
			ionOffset(MONO_H2O - 17, 2) };
	public static double Y[] = { 0, ionOffset(MONO_H2O, 1),
			ionOffset(MONO_H2O, 2) };

	private static double ionOffset(double offset, double charge) {
		return (0.5 + ((charge) * MassConstants.PROTON + (offset))
				/ ((charge) * MassConstants.BIN_WIDTH));
	}
}
