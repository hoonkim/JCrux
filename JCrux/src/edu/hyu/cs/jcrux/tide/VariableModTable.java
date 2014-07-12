package edu.hyu.cs.jcrux.tide;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.hyu.cs.jcrux.Carp;
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
	private LinkedList<Integer> mMaxCounts;
	private int mOffset;
	private ModCoder mCoder;

	private ModTable mPbModTable;
	private ModTable mPbNtpepModTable;
	private ModTable mPbCtpepModTable;
	private ModTable mPbNtproModTable;
	private ModTable mPbCtproModTable;

	public static enum MODS_SPEC_TYPE {
		MOD_SPEC, // table for regular amino acid modifications
		NTPEP, // type for n-terminal peptide modifications
		CTPEP, // type for c-terminal peptide modifications
		NTPRO, // type for n-terminal protein modifications
		CTPRO // type for c-terminal protein modifications
	};

	public VariableModTable() {
		mOffset = 0;
		mUniqueDelta = new LinkedList<Double>();
		mMaxCounts = new LinkedList<Integer>();
		mCoder = new ModCoder();

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

		if (pbModTable == mPbCtproModTable) {
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
			pbModTable = mPbCtproModTable;
			break;
		}
		if (pbModTable == null) {
			return false;
		}

		/*
		 * 이부분은 java와 C++의 Protocol Buffer에서 오는 차이 때문에 거의 새롭게 구현함.
		 */

		ModTable.Builder modTableBuilder = ModTable.newBuilder(pbModTable);

		Pattern pattern = Pattern
				.compile("[0-9]*[ACDEFGHIKLMNPQRSTVWY]+[+-]*[0-9.]+");

		Pattern pattern2 = Pattern.compile("[ACDEFGHIKLMNPQRSTVWY]+");
		Matcher matcher = pattern.matcher(specText);
		while (matcher.find()) {

			String str = matcher.group();

			int limit = 0;
			String aa;

			char plus;
			double delta;

			Matcher matcher2 = pattern2.matcher(str);
			if (matcher2.find()) {

				if (str.length() > 0 && Character.isDigit(str.charAt(0))) {
					limit = Integer
							.parseInt(str.substring(0, matcher2.start()));
				}

				aa = str.substring(matcher2.start(), matcher2.end());

				delta = Double.parseDouble(str.substring(matcher2.end() + 2,
						str.length()));

				plus = str.charAt(matcher2.end());

				if (plus == '-') {
					delta *= -1;
				}
				Modification.Builder modification = Modification.newBuilder();

				modification.setAminoAcids(aa);
				modification.setDelta(delta);

				
				
				if (limit > 1 && (modTable != MODS_SPEC_TYPE.MOD_SPEC)) {
					limit = 1;

				}
				if (limit == 0 && (modTable == MODS_SPEC_TYPE.MOD_SPEC)) {
					
					
					modTableBuilder.addStaticMod(modification);
				} else {
					modification.setMaxCount(limit);
					modTableBuilder.addVariableMod(modification);
					mUniqueDelta.addLast(delta);
					mMaxCounts.addLast(limit);
				}

			}

		}

		switch (modTable) {
		case MOD_SPEC:
			mPbModTable = modTableBuilder.build();
			break;
		case NTPEP:
			mPbNtpepModTable = modTableBuilder.build();
			break;
		case CTPEP:
			mPbCtpepModTable = modTableBuilder.build();
			break;
		case NTPRO:
			mPbNtproModTable = modTableBuilder.build();
			break;
		case CTPRO:
			mPbCtproModTable = modTableBuilder.build();
			break;
		}

		return true;
	}

	void clearTables() {
		mPbModTable = ModTable.newBuilder().clear().build();
		mPbNtpepModTable = ModTable.newBuilder().clear().build();
		mPbCtpepModTable = ModTable.newBuilder().clear().build();
		mPbNtproModTable = ModTable.newBuilder().clear().build();
		mPbCtproModTable = ModTable.newBuilder().clear().build();
	}

	int numPoss(char aa, MODS_SPEC_TYPE modTable) {
		if (modTable == null) {
			modTable = MODS_SPEC_TYPE.MOD_SPEC;
		}
		switch (modTable) {
		case MOD_SPEC:
			return mPossibles[aa].size();
		case NTPEP:
			return mPossiblesNtpe[aa].size();
		case CTPEP:
			return mPossiblesCtpe[aa].size();
		case NTPRO:
			return mPossiblesNtpr[aa].size();
		case CTPRO:
			return mPossiblesCtpr[aa].size();
		}
		return 0;
	}

	int possMaxCt(char aa, int index, MODS_SPEC_TYPE modTable) {
		if (modTable == null) {
			modTable = MODS_SPEC_TYPE.MOD_SPEC;
		}
		switch (modTable) {
		case MOD_SPEC:
			return mPossibles[aa].get(index).second;
		case NTPEP:
			return mPossiblesNtpe[aa].get(index).second;
		case CTPEP:
			return mPossiblesCtpe[aa].get(index).second;
		case NTPRO:
			return mPossiblesNtpr[aa].get(index).second;
		case CTPRO:
			return mPossiblesCtpr[aa].get(index).second;
		}
		return 0;
	}

	int possDeltIx(char aa, int index, MODS_SPEC_TYPE modTable) {
		if (modTable == null) {
			modTable = MODS_SPEC_TYPE.MOD_SPEC;
		}
		switch (modTable) {
		case MOD_SPEC:
			return mPossibles[aa].get(index).first;
		case NTPEP:
			return mPossiblesNtpe[aa].get(index).first;
		case CTPEP:
			return mPossiblesCtpe[aa].get(index).first;
		case NTPRO:
			return mPossiblesNtpr[aa].get(index).first;
		case CTPRO:
			return mPossiblesCtpr[aa].get(index).first;
		}
		return 0;
	}

	double possDelta(char aa, int index) {
		return mUniqueDelta.get(possDeltIx(aa, index, null));
	}

	boolean serializeUniqueDeltas() {
		if (mUniqueDelta.size() == 0) {
			return false;
		}
		mOriginalDeltas = new LinkedList<Double>(mUniqueDelta);

		LinkedHashSet<Double> temp = new LinkedHashSet<Double>(mUniqueDelta);
		mUniqueDelta = new LinkedList<Double>(temp);
		mCoder.init(mUniqueDelta.size());

		Init(mPbModTable);
		Init(mPbCtpepModTable);
		Init(mPbNtpepModTable);
		Init(mPbCtproModTable);
		Init(mPbNtproModTable);
		
		return true;
	}

	boolean serializeUniqueDeltas(ModTable pbModTable, MODS_SPEC_TYPE modTable) {

		if (pbModTable.getUniqueDeltasCount() == 0) {
			ModTable.Builder modTableBuilder = ModTable.newBuilder(pbModTable);
			for (double delta : mUniqueDelta) {
				modTableBuilder.addUniqueDeltas(delta);
			}
			switch (modTable) {
			case MOD_SPEC:
				mPbModTable = modTableBuilder.build();
				break;
			case NTPEP:
				mPbNtpepModTable = modTableBuilder.build();
				break;
			case CTPEP:
				mPbCtpepModTable = modTableBuilder.build();
				break;
			case NTPRO:
				mPbNtproModTable = modTableBuilder.build();
				break;
			case CTPRO:
				mPbCtproModTable = modTableBuilder.build();
				break;
			}

			return true;
		}
		if (pbModTable.getUniqueDeltasCount() != mUniqueDelta.size()) {
			return false;
		}
		for (int i = 0; i < mUniqueDelta.size(); ++i) {
			if (mUniqueDelta.get(i) != pbModTable.getUniqueDeltas(i)) {
				return false;
			}
		}

	

		return true;
	}

	ModTable parsedModTable() {
		return mPbModTable;
	}

	LinkedList<Integer> maxCounts() {
		return mMaxCounts;
	}

	LinkedList<Double> originalDeltas() {
		return mOriginalDeltas;
	}

	int encodeMod(int aaIndex, int uniqueDeltaIndex) {
		return mCoder.encodeMod(aaIndex, uniqueDeltaIndex);
	}

	// TODO show 구현 디버그용이라 안함.

	// void show () {
	// }

	int uniqueDeltaSize() {
		return mUniqueDelta.size();
	}

	static boolean isAA(char c) {
		final String AA = "ACDEFGHIKLMNPQRSTVWYX";
		return AA.contains(Character.toString(c));

	}
}
