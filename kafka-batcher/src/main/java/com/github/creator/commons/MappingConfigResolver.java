package com.github.creator.commons;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

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
