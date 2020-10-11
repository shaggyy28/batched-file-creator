package com.github.creator.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.creator.commons.MappingConfigResolver;

@Service
public class KafkaMessageProducerService {
	
	@Autowired
	private KafkaTemplate<Integer, String> template;
	
	public boolean produce(JsonNode node) {
		
		Map<String, String> topicEventMapping = MappingConfigResolver.getMappingConfigVo().getTopicEventMapping();
	
		String eventType = node.get("event_type").asText();
		if(node.get("data").isContainerNode()) {
			JsonNode data = node.get("data");
			String topicName = topicEventMapping.get(eventType);
			template.send(topicName, data.toString());
		}
		return true;
	}
}
