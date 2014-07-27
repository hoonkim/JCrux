package edu.hyu.cs.jcrux.tide.records;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.google.protobuf.Message;

import edu.hyu.cs.jcrux.Carp;
import edu.hyu.cs.pb.HeaderPB.Header;

public class HeadedRecordWriter {
	private RecordWriter mWriter;

	public HeadedRecordWriter(final String fileName, final Header header,
			final int bufSize) {
		try {
			mWriter = new RecordWriter(fileName, bufSize);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		if (!mWriter.ok()) {
			Carp.carp(Carp.CARP_DEBUG,
					"HeadedRecordWriter 생성자에서 mWriter가 제대로 안생김");
		}
		write(header);

	}

	public HeadedRecordWriter(final String fileName, final Header header) {
		try {
			mWriter = new RecordWriter(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		if (!mWriter.ok()) {
			Carp.carp(Carp.CARP_DEBUG,
					"HeadedRecordWriter 생성자에서 mWriter가 제대로 안생김");
		}
		write(header);
	}

	public HeadedRecordWriter(final FileOutputStream rawOutput,
			final Header header) {
		mWriter = new RecordWriter(rawOutput);
		write(header);
	}

	public boolean write(final Message message) {
		return mWriter.write(message);
	}
	
	public boolean finish(){
		return mWriter.finish();
	}
}
