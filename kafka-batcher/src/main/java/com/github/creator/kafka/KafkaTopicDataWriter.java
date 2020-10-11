package com.github.creator.kafka;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.github.creator.vo.TopicWriterVo;

@Service
public class KafkaTopicDataWriter{
	
	@Autowired
	@Qualifier(value = "osMap")
	private Map<String, TopicWriterVo> fileNameMap;
	
	@PreDestroy
	public void preDestrou() {
		fileNameMap.values().forEach(e -> {
			try {
				e.getOutputStream().close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
	}
	
	public void write(String message, String topic) {
		
		try {
			TopicWriterVo topicWriterVo = fileNameMap.get(topic);
			byte[] bytes = message.replace("\n", "").getBytes();
			topicWriterVo.getOutputStream().write(bytes);
			topicWriterVo.getOutputStream().write('\n');
			topicWriterVo.incrementByteSize(bytes.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
