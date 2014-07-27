package edu.hyu.cs.jcrux.tide.records;

import edu.hyu.cs.pb.HeaderPB.Header;
import edu.hyu.cs.pb.PeptidesPB.Peptide;
import edu.hyu.cs.pb.RawProteinsPB.Protein;

public class HeadedRecordReader {
	private RecordReader mReader;
	private Header mHeader;
	private boolean mDelHeader;

	public HeadedRecordReader(final String fileName, final Header header,
			final int bufSize) {
		mReader = new RecordReader(fileName, bufSize);
		mHeader = null;

		mDelHeader = (header == null);

		if (!done()) {
			mHeader = read(mHeader);
		}
	}

	public HeadedRecordReader(final String fileName, final Header header) {
		mReader = new RecordReader(fileName);
		mHeader = header;
		mDelHeader = (header == null);
		if (!done()) {
			mHeader = read(mHeader);
			// System.out.println(mHeader.getFileType());
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

	public Header read(Header message) {
		System.out.print("Headed ");
		return mReader.read(message);
	}

	public Protein read() {
		return mReader.read();
	}

	public Header getHeader() {
		return mHeader;
	}
	
	public Peptide readPeptide(){
		return mReader.readPeptide();
	}
}
