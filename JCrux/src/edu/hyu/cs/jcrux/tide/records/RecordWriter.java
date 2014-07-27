package edu.hyu.cs.jcrux.tide.records;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Message;

import edu.hyu.cs.jcrux.Carp;

public class RecordWriter {
	public static final int MAGIC_NUMBER = 0xfead1234;

	private CodedOutputStream mCodedOutput = null;
	private FileOutputStream mRawOutput = null;
	private int mBufferSize = -1;

	public RecordWriter(final String fileName, int bufSize)
			throws FileNotFoundException {
		File file = new File(fileName);
		System.out.println(fileName);

		mRawOutput = new FileOutputStream(file, false);
		mBufferSize = bufSize;
		init();

	}

	public RecordWriter(final String fileName) throws FileNotFoundException {
		this(fileName, -1);
	}

	public RecordWriter(final FileOutputStream rawOutput) {
		mRawOutput = rawOutput;
		init();
		mRawOutput = null;

	}

	public boolean ok() {
		return null != mCodedOutput;
	}

	public boolean write(final Message message) {

		try {
			/* WriteVarint32(message->ByteSize())를 대신하였음 */
			mCodedOutput.writeRawVarint32(message.getSerializedSize());

		} catch (IOException e) {
			mCodedOutput = null;
			Carp.carp(Carp.CARP_DEBUG, "mCodedOutput error");
			return false;
		}

		try {
			/* SerializeWithCachedSizes를 writeTo로 대신하였는데 맞는지 모르겠음. */
			message.writeTo(mCodedOutput);
			mCodedOutput.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	public boolean finish() {

		System.out.println("Finish!!");
		try {
			/* WriteVarint32(message->ByteSize())를 대신하였음 */
			mCodedOutput.writeRawVarint32(0);
			mCodedOutput.flush();

		} catch (IOException e) {
			mCodedOutput = null;
			Carp.carp(Carp.CARP_DEBUG, "mCodedOutput error");
			return false;
		}

		return true;
	}

	private void init() {
		mCodedOutput = CodedOutputStream.newInstance(mRawOutput);
		try {
			mCodedOutput.writeRawLittleEndian32(MAGIC_NUMBER);
		} catch (IOException e) {
			mCodedOutput = null;
			Carp.carp(Carp.CARP_DEBUG, "mCodedOutput error");
		}
	}
}
