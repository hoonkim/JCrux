package edu.hyu.cs.jcrux.tide.records;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.Message;

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
			CodedInputStream codedInput = CodedInputStream
					.newInstance(mRawInput);
			int magicNumber;
			magicNumber = codedInput.readInt32();
			if (magicNumber == MAGIC_NUMBER) {
				mValid = true;
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
		mCodedInput = CodedInputStream.newInstance(mRawInput);
		try {
			mSize = mCodedInput.readRawVarint32();
		} catch (IOException e) {
			mValid = false;
			return mValid;
		}

		return (mSize == 0);
		// TODO 260: there should be an easy way to tell if we're at the end
		// of the file, but I can't seem to find a reliable way to do that
		// easily...
	}

	public boolean read(Message message) {
		if (mValid) {
			return false;
		}

		try {
			int limit = mCodedInput.pushLimit(mSize);
			message.getParserForType().parseFrom(mCodedInput);
			mCodedInput.skipMessage();
			mCodedInput.getBytesUntilLimit();
			mCodedInput.popLimit(limit); // for completness); perhaps remove
			mCodedInput = null;
			mSize = Integer.MAX_VALUE;
		} catch (IOException e) {
			mValid = false;
			return mValid;
		}
		return true;
	}
}
