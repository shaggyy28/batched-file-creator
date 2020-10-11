package com.github.creator.vo;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.creator.commons.MappingDeserializer;

import lombok.Data;

@Data
public class MappingConfigVo {
	
	
	@JsonProperty("filename_topic_mapping")
	@JsonDeserialize(using = MappingDeserializer.class)
	private Map<String, String> fileNameTopicMapping;
	
	@JsonProperty("topic_event_mapping")
	@JsonDeserialize(using = MappingDeserializer.class)
	private Map<String, String> topicEventMapping;
}
