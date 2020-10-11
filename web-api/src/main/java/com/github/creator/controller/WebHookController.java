package com.github.creator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.creator.service.KafkaMessageProducerService;

@Controller
public class WebHookController {
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private KafkaMessageProducerService producer;
	
	@PostMapping(value = "/post-data")
	public ResponseEntity<JsonNode> webHook(@RequestBody JsonNode body){
		
		if(producer.produce(body)) {
			ObjectNode response = mapper.createObjectNode();
			response.put("status", "sucess");
			return ResponseEntity.ok().body(response);
		}else {
			ObjectNode response = mapper.createObjectNode();
			response.put("status", "failed");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
}
