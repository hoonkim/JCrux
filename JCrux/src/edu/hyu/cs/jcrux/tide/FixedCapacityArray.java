package edu.hyu.cs.jcrux.tide;

import java.util.Arrays;
import java.util.Iterator;

public class FixedCapacityArray<C> implements Iterable<C> {

	private C mData[];
	private int mSize;
	
	@SuppressWarnings("unchecked")
	public void init(int capacity) {
		mSize = 0;
		mData = (C[]) new Object[capacity];
	}

	@SuppressWarnings("unchecked")
	public FixedCapacityArray(int capacity) {
		mSize = 0;
		mData = (C[]) new Object[capacity];
	}

	public FixedCapacityArray() {
		mData = null;
		mSize = 0;
	}

	public void clear() {
		mSize = 0;
	}

	public boolean empty() {
		return mSize == 0;
	}

	public C get(int index) {
		return mData[index];
	}

	public C[] data() {
		return mData;
	}

	int size() {
		return mSize;
	}

	void setSize(int size) {
		mSize = size;
	}

	void addLast(C data) {
		mData[mSize++] = data;
	}

	C getLast() {
		if (mSize > 0) {
			return mData[mSize - 1];
		} else {
			return null;
		}
	}

	@Override
	public Iterator<C> iterator() {
		return Arrays.asList(mData).iterator();
	}

}
