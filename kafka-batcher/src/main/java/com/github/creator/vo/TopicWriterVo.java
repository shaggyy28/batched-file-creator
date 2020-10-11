package com.github.creator.vo;

import java.io.OutputStream;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TopicWriterVo {
	
	private OutputStream 	outputStream;
	private String 			fileNameFormat;
	private long 			createdTimeEpoch;
	private long   			bytes;
	private int				fileId;			
	
	public void incrementByteSize(int byteSize) {
		this.bytes += byteSize;
	}
	
	public void incrementFileId() {
		this.fileId += 1;
	}
	
	public String getFileName() {
		return String.format(this.fileNameFormat, this.fileId);
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
	
	
}
