package edu.hyu.cs.jcrux.tide;

import java.lang.invoke.SwitchPoint;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import edu.hyu.cs.pb.HeaderPB.ModTable;
import edu.hyu.cs.pb.HeaderPB.Modification;
import edu.hyu.cs.types.Pair;

public class VariableModTable {

	/**
	 * Typdef 비슷하게 해줌.
	 * 
	 * @author HoonKim
	 * 
	 */
	class IntPairVec extends LinkedList<Pair<Integer, Integer>> {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3651953566242720886L;

	}

	private IntPairVec mPossibles[] = new IntPairVec[256];
	private IntPairVec mPossiblesCtpe[] = new IntPairVec[256];
	private IntPairVec mPossiblesNtpe[] = new IntPairVec[256];
	private IntPairVec mPossiblesCtpr[] = new IntPairVec[256];
	private IntPairVec mPossiblesNtpr[] = new IntPairVec[256];

	private LinkedList<Double> mUniqueDelta, mOriginalDeltas;
	private LinkedList<Integer> maxCounts;
	private int mOffset;
	private ModCoder mCoder;

	private ModTable mPbModTable;
	private ModTable mPbNtpepModTable;
	private ModTable mPbCtpepModTable;
	private ModTable mPbNtproModTable;
	private ModTable mPbCtrpoModTable;

	public static enum MODS_SPEC_TYPE {
		MOD_SPEC, // table for regular amino acid modifications
		NTPEP, // type for n-terminal peptide modifications
		CTPEP, // type for c-terminal peptide modifications
		NTPRO, // type for n-terminal protein modifications
		CTPRO // type for c-terminal protein modifications
	};

	public VariableModTable() {
		mOffset = 0;
	}

	public boolean Init(final ModTable pbModTable) {
		IntPairVec possibles[] = null;

		if (pbModTable == mPbModTable) {
			possibles = mPossibles;
		}

		if (pbModTable == mPbNtpepModTable) {
			possibles = mPossiblesNtpe;
		}

		if (pbModTable == mPbCtpepModTable) {
			possibles = mPossiblesCtpe;
		}

		if (pbModTable == mPbNtproModTable) {
			possibles = mPossiblesNtpr;
		}

		if (pbModTable == mPbCtrpoModTable) {
			possibles = mPossiblesCtpr;
		}

		if (pbModTable.getVariableModCount() == 0) {
			return true;
		}

		LinkedList<Double> UD = mUniqueDelta;
		HashMap<Double, Integer> deltas = new HashMap<Double, Integer>();

		for (int i = 0; i < UD.size(); ++i) {
			deltas.put(UD.get(i), i);
		}

		for (int i = 0; i < pbModTable.getVariableModCount(); ++i) {
			Modification mod = pbModTable.getVariableMod(i);
			String aa = mod.getAminoAcids();
			for (int j = 0; j < aa.length(); ++j) {
				possibles[aa.charAt(j)].addLast(Pair.of(
						deltas.get(mod.getDelta()), i + mOffset));
			}
		}

		mOffset += pbModTable.getVariableModCount();

		for (int i = 0; i < 256; i++) {
			LinkedList<Pair<Integer, Integer>> p = possibles[i];
			if (!isAA((char) i) && p.size() > 0) {
				return false;
			}

			Collections.sort(p);
			for (int j = 0; j < p.size() - 1; j++) {
				if (p.get(i).equals(p.get(i + 1))) {
					System.err.printf(
							"ERROR: Amino acid modification %c + %.5f"
									+ " appears more than once in "
									+ "modifications table.\n", ((char) i),
							mUniqueDelta.get(p.get(j).first));
					return false;
				}
			}
		}
		return true;
	}

	public boolean parse(final String specText, MODS_SPEC_TYPE modTable) {
		if (modTable == null) {
			modTable = MODS_SPEC_TYPE.MOD_SPEC;
		}

		ModTable pbModTable = null;

		switch (modTable) {
		case MOD_SPEC:
			pbModTable = mPbModTable;
			break;
		case NTPEP:
			pbModTable = mPbNtpepModTable;
			break;
		case CTPEP:
			pbModTable = mPbCtpepModTable;
			break;
		case NTPRO:
			pbModTable = mPbNtproModTable;
			break;
		case CTPRO:
			// TODO 이부분 왠지 제작자가 실수한것 같습니다.
			// 본래 소스는
			// pbModTable = mPbNtproModTable;
			pbModTable = mPbCtrpoModTable;
			break;
		}
		if (pbModTable == null) {
			return false;
		}

		int post = 0;
		// TODO 밑에 구현.

		return true;
	}

	static boolean isAA(char c) {
		final String AA = "ACDEFGHIKLMNPQRSTVWYX";
		return AA.contains(Character.toString(c));
	}
}
