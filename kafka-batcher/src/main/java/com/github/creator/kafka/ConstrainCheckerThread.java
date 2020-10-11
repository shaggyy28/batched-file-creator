package com.github.creator.kafka;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.util.Map;
import java.util.Map.Entry;

import com.github.creator.vo.TopicWriterVo;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ConstrainCheckerThread implements Runnable{

	private Map<String, TopicWriterVo> fileNameMap;
	private String timeThreshold;
	private long sizeThreshold;
	
	private static final long SECOND_EPOCH_MILLIS = 1000L; 
	private static final long MINUTES_EPOCH_MILLIS = 6000L;
	
	@Override
	public void run() {
		assert timeThreshold.matches("[0-9]+[sm]$");
		int timeUnit = Integer.parseInt(timeThreshold.replaceAll("[sm]$", ""));
		long timeThresholdEpoch = timeThreshold.endsWith("s") ? timeUnit * SECOND_EPOCH_MILLIS : timeUnit * MINUTES_EPOCH_MILLIS;
		while (true) {
			for (Entry<String, TopicWriterVo> ent : fileNameMap.entrySet()) {
				TopicWriterVo topicWriterVo = ent.getValue();
				if (topicWriterVo.getBytes() > sizeThreshold
						|| (System.currentTimeMillis() - topicWriterVo.getCreatedTimeEpoch() > timeThresholdEpoch)) {
					topicWriterVo.incrementFileId();
					try (FileOutputStream fis = new FileOutputStream(
							String.format(topicWriterVo.getFileNameFormat(), topicWriterVo.getFileId()));
							BufferedOutputStream bis = new BufferedOutputStream(fis);
							FilterOutputStream os = new FilterOutputStream(bis);) {
							topicWriterVo.setOutputStream(os);
							topicWriterVo.setCreatedTimeEpoch(System.currentTimeMillis());
					} catch (Exception e) {
					}
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}