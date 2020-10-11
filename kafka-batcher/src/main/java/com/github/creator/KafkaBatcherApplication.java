package com.github.creator;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;

import com.github.creator.commons.MappingConfigResolver;
import com.github.creator.kafka.ConstrainCheckerThread;
import com.github.creator.kafka.KafkaTopicMessageListner;
import com.github.creator.vo.TopicWriterVo;

@SpringBootApplication
public class KafkaBatcherApplication {

	@Value("${batched.file.creator.data.path}")
	private String filePath;
	
	@Value("${batched.file.creator.time.threshold}")
	private String timeThreshold;
	
	@Value("${batched.file.creator.size.threshold}")
	private String sizeThreshold;
	
	public static void main(String[] args) {
		SpringApplication.run(KafkaBatcherApplication.class, args);
	}
	
	@Bean
	public KafkaMessageListenerContainer<Integer, String> foo(@Autowired KafkaTopicMessageListner kafkaTopicMessageListner){
		ContainerProperties containerProps = new ContainerProperties(MappingConfigResolver.getTopics());
		containerProps.setMessageListener(kafkaTopicMessageListner);
		containerProps.setGroupId("1");
		DefaultKafkaConsumerFactory<Integer, String> cf =
		                        new DefaultKafkaConsumerFactory<>(consumerProps());
		return new KafkaMessageListenerContainer<>(cf, containerProps);
	}
	
	@Bean
	public TaskExecutor taskExecutor() {
	    return new SimpleAsyncTaskExecutor(); // Or use another one of your liking
	}
	
	@Bean
	public CommandLineRunner schedulingRunner(TaskExecutor executor, @Autowired
			@Qualifier(value = "osMap")Map<String, TopicWriterVo> fileNameMap) {
	    return new CommandLineRunner() {
	        public void run(String... args) throws Exception {
	            executor.execute(new ConstrainCheckerThread(fileNameMap, timeThreshold, Long.parseLong(sizeThreshold)));
	        }
	    };
	}
	
	
	@Bean
	@Qualifier(value = "osMap")
	public Map<String, TopicWriterVo> getOutputStreamFileTopic(){
		
		Map<String, TopicWriterVo> osMap = new HashMap<>();
		for(Entry<String, String> ent : MappingConfigResolver.getMappingConfigVo().getTopicFileNameMapping().entrySet()) {
			String fileNameFormat = ent.getValue();
			String topicName = ent.getKey();
			try {
				FileOutputStream fis = new FileOutputStream(filePath + "/" + String.format(fileNameFormat, 0));
//				BufferedOutputStream bis = new BufferedOutputStream(fis);
//				FilterOutputStream os = new FilterOutputStream(bis);
				osMap.put(topicName, TopicWriterVo
					.builder()
					.fileId(0)
					.fileNameFormat(fileNameFormat)
					.createdTimeEpoch(System.currentTimeMillis())
					.outputStream(fis)
					.build()
				);
					
			} catch (IOException e) {
					e.printStackTrace();
			}
		}
		return osMap;
	}
	
	
	private Map<String, Object> consumerProps() {
	    Map<String, Object> props = new HashMap<>();
	    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
	    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
	    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
	    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
	    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
	    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
	    return props;
	}
	
	
}
