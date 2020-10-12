package com.github.creator.kafka;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.creator.commons.MappingConfigResolver;
import com.github.creator.vo.TopicWriterVo;

@Service
public class KafkaTopicDataWriter{
	
	@Autowired
	@Qualifier(value = "osMap")
	private Map<String, TopicWriterVo> fileNameMap;
	
	private static final ObjectMapper MAPPER = new ObjectMapper(); 
	
	@PreDestroy
	public void preDestroy() {
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
			Boolean flattenedFlag = MappingConfigResolver.getMappingConfigVo().getTopicFlattenedFlagMapping().get(topic);
			if(flattenedFlag.booleanValue()) {
				message = flattenJson(message);
			}
			byte[] bytes = message.replace("\n", "").getBytes();
			topicWriterVo.getOutputStream().write(bytes);
			topicWriterVo.getOutputStream().write('\n');
			topicWriterVo.incrementByteSize(bytes.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String flattenJson(String message) {
		
		List<String> flatList = new ArrayList<>();
		try {
			JsonNode tree = MAPPER.readTree(message);
			for (JsonNode field : tree) {
				flatList.add(field.asText());
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return String.join("\001", flatList);
	}
	
}
