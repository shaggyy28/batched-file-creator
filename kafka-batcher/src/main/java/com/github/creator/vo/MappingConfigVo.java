package com.github.creator.vo;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.creator.commons.MappingDeserializer;

import lombok.Data;

@Data
public class MappingConfigVo {
	
	
	@JsonProperty("topic_filename_mapping")
	@JsonDeserialize(using = MappingDeserializer.class)
	private Map<String, String> topicfileNameMapping;
	
	@JsonProperty("event_topic_mapping")
	@JsonDeserialize(using = MappingDeserializer.class)
	private Map<String, String> eventTopicMapping;
	
	@JsonIgnore
	private Map<String, Boolean> topicFlattenedFlagMapping;
}
