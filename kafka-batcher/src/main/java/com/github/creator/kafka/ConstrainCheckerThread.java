package com.github.creator.kafka;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.Assert;

import com.github.creator.commons.AppLogger;
import com.github.creator.vo.TopicWriterVo;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ConstrainCheckerThread implements Runnable{

	private Map<String, TopicWriterVo> fileNameMap;
	private String timeThreshold;
	private String sizeThreshold;
	private String dataDir;
	
	private static final long SECOND_EPOCH_MILLIS = 1000L; 
	private static final long MINUTES_EPOCH_MILLIS = 6000L;
	
	@Override
	public void run() {
		int timeUnit = 0;
		int sizeUnit = 0;
		if(timeThreshold.matches("[0-9]+(s|m)$"))
			timeUnit = Integer.parseInt(timeThreshold.replaceAll("[sm]$", ""));
		if(sizeThreshold.matches("[0-9]+(KB|MB)$"))
			sizeUnit = Integer.parseInt(sizeThreshold.replaceAll("KB|MB$", ""));
		long timeThresholdEpoch = timeThreshold.endsWith("s") ? timeUnit * SECOND_EPOCH_MILLIS : timeUnit * MINUTES_EPOCH_MILLIS;
		long sizeBytes = sizeThreshold.endsWith("KB") ? sizeUnit * 1024 : sizeUnit * 1024 * 1024;
		while (true) {
			try {
			for (Entry<String, TopicWriterVo> ent : fileNameMap.entrySet()) {
				TopicWriterVo topicWriterVo = ent.getValue();
				if (
						topicWriterVo.getBytes() != 0 &&
						(topicWriterVo.getBytes() > sizeBytes ||
						(System.currentTimeMillis() - topicWriterVo.getCreatedTimeEpoch() > timeThresholdEpoch))
					) {
					long currentTimeMillis = System.currentTimeMillis();
					topicWriterVo.incrementFileId();
					FileOutputStream fis = new FileOutputStream(dataDir + "/" + String.format(topicWriterVo.getFileNameFormat(), topicWriterVo.getFileId()));
					BufferedOutputStream bis = new BufferedOutputStream(fis);
					AppLogger.info(topicWriterVo.getBytes()+"");
					topicWriterVo.getOutputStream().close();
					topicWriterVo.setOutputStream(bis);
					topicWriterVo.setCreatedTimeEpoch(currentTimeMillis);
					topicWriterVo.setBytes(0);
				}
			}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}