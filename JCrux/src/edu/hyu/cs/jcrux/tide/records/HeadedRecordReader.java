package edu.hyu.cs.jcrux.tide.records;

import com.google.protobuf.Message;

import edu.hyu.cs.pb.HeaderPB.Header;

public class HeadedRecordReader {
	private RecordReader mReader;
	private Header mHeader;
	private boolean mDelHeader;

	public HeadedRecordReader(final String fileName, final Header header,
			final int bufSize) {
		mReader = new RecordReader(fileName, bufSize);
		mHeader = header;
		mDelHeader = (header == null);
		if (header == null) {
			mHeader = Header.getDefaultInstance();
		}
		if (!done()) {
			read(mHeader);
		}
	}

	public RecordReader reader() {
		return mReader;
	}

	public boolean ok() {
		return mReader.ok();
	}

	public boolean done() {
		return mReader.done();
	}

	public boolean read(final Message message) {
		return mReader.read(message);
	}

	public Header getHeader() {
		return mHeader;
	}
}
