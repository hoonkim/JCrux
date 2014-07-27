package edu.hyu.cs.jcrux.tide.records;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.InvalidProtocolBufferException;

import edu.hyu.cs.jcrux.Carp;
import edu.hyu.cs.pb.HeaderPB.Header;
import edu.hyu.cs.pb.PeptidesPB.Peptide;
import edu.hyu.cs.pb.RawProteinsPB.Protein;

public class RecordReader {
	public static final int MAGIC_NUMBER = 0xfead1234;
	private FileInputStream mRawInput = null;
	private CodedInputStream mCodedInput = null;
	private int mSize = Integer.MAX_VALUE;
	private int mBufferSize = -1;
	boolean mValid = false;

	public RecordReader(final String fileName, final int bufSize) {
		try {
			mRawInput = new FileInputStream(fileName);
			mBufferSize = bufSize;
			mCodedInput = CodedInputStream.newInstance(mRawInput);
			int magicNumber;
			magicNumber = mCodedInput.readRawLittleEndian32();
			System.out.printf("%x\n", magicNumber);
			if (magicNumber == MAGIC_NUMBER) {
				mValid = true;
				Carp.carp(Carp.CARP_DEBUG, "magic number is valid");
			} else {
				Carp.carp(Carp.CARP_DEBUG, "magic number is invalid");
			}

		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {
			mValid = false;
			e.printStackTrace();
		}
	}

	public RecordReader(final String fileName) {
		this(fileName, -1);
	}

	public boolean ok() {
		return mValid;
	}

	public boolean done() {
		if (!mValid) {
			return true;
		}
		try {
			mSize = mCodedInput.readRawVarint32();
		} catch (IOException e) {
			System.out.println("false!");
			mValid = true;
			return mValid;
		}

		return (mSize == 0);
		// TODO 260: there should be an easy way to tell if we're at the end
		// of the file, but I can't seem to find a reliable way to do that
		// easily...
	}

	public Header read(Header message) {

		System.out.print("readdd ");
		if (!mValid) {
			return null;
		}

		try {
			System.out.println("Size: " + mSize);
			byte arr[] = mCodedInput.readRawBytes(mSize);
			Header header = Header.parseFrom(arr);

			return header;

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public Protein read() {
		if (!mValid) {
			return null;
		}

		try {
			byte arr[] = mCodedInput.readRawBytes(mSize);
			Protein protein = Protein.parseFrom(arr);

			return protein;

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Peptide readPeptide() {
		if (!mValid) {
			return null;
		}

		try {
			byte arr[] = mCodedInput.readRawBytes(mSize);
			Peptide peptide = Peptide.parseFrom(arr);

			return peptide;

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
