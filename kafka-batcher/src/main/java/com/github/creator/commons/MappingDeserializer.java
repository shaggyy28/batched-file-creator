package com.github.creator.commons;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MappingDeserializer extends JsonDeserializer<Map<String, String>> {

	@Override
	public Map<String, String> deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException {
		JsonNode tree = (JsonNode)jp.readValueAsTree();
		Map<String, String> map = new HashMap<>();
		for(JsonNode arr : tree) {
			map.put(
					arr.has("event_type") ? arr.get("event_type").asText() : arr.get("topic_name").asText(),
					arr.has("topic_name") ? arr.get("topic_name").asText() : arr.get("filename_pattern").asText()
			);
		}
		return map;
	}
}
