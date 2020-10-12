package com.github.creator.commons;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.creator.vo.MappingConfigVo;

@Component
public class MappingConfigResolver {
	
	private static final ObjectMapper mapper = new ObjectMapper(); 
	
	@Value("classpath:mapping-config.json")
	private Resource mappingConfigFile;
	
	private static MappingConfigVo mappingConfigVo;
	
	private static String[] topics;

	
	@PostConstruct
	public void process() throws Exception {
		try {
			mappingConfigVo = mapper.readValue(mappingConfigFile.getInputStream(), MappingConfigVo.class);
			topics = new String[mappingConfigVo.getEventTopicMapping().values().size()];
			mappingConfigVo.getEventTopicMapping().values().toArray(topics);

			Map<String, Boolean> topicFlattenMap = new HashMap<>();
			for(JsonNode nd : mapper.readTree(mappingConfigFile.getInputStream()).get("topic_filename_mapping")) {
				topicFlattenMap.put(
						nd.get("topic_name").asText(), 
						nd.get("flattened_data").asBoolean()
				);
			}
			mappingConfigVo.setTopicFlattenedFlagMapping(topicFlattenMap);
		
		} catch (IOException e) {
			throw new Exception("Unable to load config file");
		}
		
	}
	
	public static MappingConfigVo getMappingConfigVo() {
		return mappingConfigVo;
	}
	
	public static String[] getTopics(){
		return topics;
	}

}
