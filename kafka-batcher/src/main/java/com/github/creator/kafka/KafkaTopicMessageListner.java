package com.github.creator.kafka;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaTopicMessageListner implements MessageListener<Integer, String> {
	
	@Autowired
	private KafkaTopicDataWriter kafkaTopicDataWriter;
	
	@Override
	public void onMessage(ConsumerRecord<Integer, String> message) {
		
		kafkaTopicDataWriter.write(message.value(), message.topic());
		
	}
}


