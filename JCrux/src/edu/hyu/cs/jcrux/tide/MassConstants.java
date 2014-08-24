package edu.hyu.cs.jcrux.tide;

import edu.hyu.cs.jcrux.Carp;
import edu.hyu.cs.pb.HeaderPB.ModTable;
import edu.hyu.cs.pb.HeaderPB.Modification;

public class MassConstants {
	public static final double PROTON = 1.00727646688;
	public static final double BIN_WIDTH = 1.0005079;

	public static double MONO_TABLE[] = new double[256];
	public static double AVG_TABLE[] = new double[256];
	public static double AA_BIN1[] = new double[256];
	public static double AA_BIN2[] = new double[256];

	public static double ELTS_MONO[] = { 
			1.007825035, // H
			12.0, // C
			14.003074, // N
			15.99491463, // O
			30.973762, // P
			31.9720707 // S
	};

	public static double ELTS_AVG[] = {
			1.00794, // H
			12.0107, // C
			14.0067, // N
			15.9994, // O
			30.973761, // P
			32.065 // S
	};

	public static double K_FIXED_POINT_SCALAR = 1e5;

	public static final double MONO_H2O = 2 * ELTS_MONO[0] + ELTS_MONO[3];
	public static final double AVG_H2O = 2 * ELTS_AVG[0] + ELTS_AVG[3];
	public static final double MONO_NH3 = 3 * ELTS_MONO[0] + ELTS_MONO[2];
	public static final double MONO_CO = ELTS_MONO[1] + ELTS_MONO[3];

	static public int FIXP_MONO_TABLE[] = new int[256];
	static public int FIXP_AVG_TABLE[] = new int[256];

	static public int FIXP_MONO_H2O = toFixPt(MONO_H2O);
	static public int FIXP_AVG_H2O = toFixPt(AVG_H2O);

	static public int FIXP_MONO_NH3 = toFixPt(MONO_NH3);
	static public int FIXP_MONO_CO = toFixPt(MONO_CO);
	static public int FIXP_PROTON = toFixPt(PROTON);

	private static ModCoder modCoder;

	private static double[] uniqueDeltas;
	private static double[] uniqueDeltasBin;

	public static boolean init(final ModTable modTable) {
		
		if (modTable != null && checkModTable(modTable)) {
			return false;
		}

		for (int i = 0; i < 256; ++i) {
			MONO_TABLE[i] = 0;
			AVG_TABLE[i] = 0;
		}

		fillMassTable(ELTS_MONO, MONO_TABLE);
		fillMassTable(ELTS_AVG, AVG_TABLE);

		if (modTable != null) {
			
			for (int i = 0; i < modTable.getStaticModCount(); ++i) {
				char aa = modTable.getStaticMod(i).getAminoAcids().charAt(0);
				double delta = modTable.getStaticMod(i).getDelta();
				MONO_TABLE[aa] += delta;
				AVG_TABLE[aa] += delta;
			}
			// carp
			Carp.carp(Carp.CARP_DEBUG,
					"Number of unique modification masses: %d\n",
					modTable.getUniqueDeltasCount());

			modCoder = new ModCoder();

			modCoder.init(modTable.getUniqueDeltasCount());
			uniqueDeltas = new double[modTable.getUniqueDeltasCount()];
			uniqueDeltasBin = new double[modTable.getUniqueDeltasCount()];

			for (int i = 0; i < modTable.getUniqueDeltasCount(); ++i) {
				uniqueDeltas[i] = modTable.getUniqueDeltas(i);
				uniqueDeltasBin[i] = modTable.getUniqueDeltas(i);
			}

			for (int i = 0; i < 256; ++i) {
				if (MONO_TABLE[i] == 0) {
					MONO_TABLE[i] = AVG_TABLE[i] = AA_BIN1[i] = AA_BIN2[i] = Double.NaN;
					FIXP_MONO_TABLE[i] = FIXP_AVG_TABLE[i] = 0;
					
				} else {
					double bin = MONO_TABLE[i]/BIN_WIDTH;
					AA_BIN1[i] = bin;
					AA_BIN2[i] = bin/2;
					FIXP_MONO_TABLE[i] = toFixPt(MONO_TABLE[i]);
					FIXP_AVG_TABLE[i] = toFixPt(AVG_TABLE[i]);
					
				}
			}
		}

		return true;
	}

	public static boolean checkModification(final Modification mod,
			boolean repeats[]) {

		String aaStr = mod.getAminoAcids();

		if (aaStr.length() != 1) {
			return false;
		}

		char aa = aaStr.charAt(0);

		final String AA = "ACDEFGHIKLMNPQRSTVWY";

		if (AA.indexOf(aa) < -1) {
			return false;
		}

		if (repeats != null) {
			if (repeats[aa]) {
				return false;
			}
			repeats[aa] = true;
		}

		return true;
	}

	public static boolean checkModTable(final ModTable modTable) {
		boolean repeats[] = new boolean[256];
		for (int i = 0; i < 256; ++i) {
			repeats[i] = false;
		}
		for (int i = 0; i < modTable.getStaticModCount(); i++) {
			if (checkModification(modTable.getStaticMod(i), repeats)) {
				return false;
			}
		}
		return true;
	}

	static int decodeModAAIndex(int code, int aaIndex) {
		return modCoder.decodeModAAIndex(code);
	}

	static double decodeModDelta(int code) {
		int uniqueDeltaIndex;
		uniqueDeltaIndex = modCoder.decodeModUniqueDeltaIndex(code);
		return uniqueDeltasBin[uniqueDeltaIndex];
	}

	static void fillMassTable(final double elements[], double table[]) {
		final double e[] = elements;
		double H = e[0], C = e[1], N = e[2], O = e[3], P = e[4], S = e[5];

		for (int i = 0; i < 256; ++i) {
			table[i] = Double.NaN;
		}

		table['A'] = C * 3 + H * 5 + N + O;
		table['C'] = C * 3 + H * 5 + N + O + S;
		table['D'] = C * 4 + H * 5 + N + O * 3;
		table['E'] = C * 5 + H * 7 + N + O * 3;
		table['F'] = C * 9 + H * 9 + N + O;
		table['G'] = C * 2 + H * 3 + N + O;
		table['H'] = C * 6 + H * 7 + N * 3 + O;
		table['I'] = C * 6 + H * 11 + N + O;
		table['K'] = C * 6 + H * 12 + N * 2 + O;
		table['L'] = C * 6 + H * 11 + N + O;
		table['M'] = C * 5 + H * 9 + N + O + S;
		table['N'] = C * 4 + H * 6 + N * 2 + O * 2;
		table['P'] = C * 5 + H * 7 + N + O;
		table['Q'] = C * 5 + H * 8 + N * 2 + O * 2;
		table['R'] = C * 6 + H * 12 + N * 4 + O;
		table['S'] = C * 3 + H * 5 + N + O * 2;
		table['T'] = C * 4 + H * 7 + N + O * 2;
		table['V'] = C * 5 + H * 9 + N + O;
		table['W'] = C * 11 + H * 10 + N * 2 + O;
		table['Y'] = C * 9 + H * 9 + N + O * 2;

		table['w'] = H * 2 + O; // water
		table['a'] = H * 3 + N; // ammonia
		table['c'] = C + O; // carbon monoxide
	}

	public static int toFixPt(double x) {
		return (int) Math.round(x * K_FIXED_POINT_SCALAR);
	}

	public static double toDouble(int x) {
		return x / K_FIXED_POINT_SCALAR;
	}
}
