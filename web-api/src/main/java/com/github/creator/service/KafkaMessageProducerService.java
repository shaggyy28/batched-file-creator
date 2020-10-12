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
		
		Map<String, String> topicEventMapping = MappingConfigResolver.getMappingConfigVo().getEventTopicMapping();
	
		if(node.get("payload").isContainerNode()) {
			JsonNode payload = node.get("payload");
			String eventType = payload.get("event_type").asText();
			String topicName = topicEventMapping.get(eventType);
			template.send(topicName, payload.toString());
		}
		return true;
	}
}
