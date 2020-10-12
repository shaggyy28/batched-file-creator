package com.github.creator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.creator.commons.AppLogger;
import com.github.creator.commons.MappingConfigResolver;
import com.github.creator.controller.WebHookController;

@SpringBootTest
@AutoConfigureMockMvc
class BatchedFileCreatorApplicationTests {
	
	@Autowired
	WebHookController WebHookController;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Value("${batched.file.creator.data.dir}")
	private String dataDir;
	
	
	@Test
	public void publishData() throws Exception {
		if(Files.exists(Paths.get(dataDir + File.separator + "data_check"))) {
			AppLogger.info("delete the existing data_check dir then run publishData test");
			throw new Exception("");
		}
		int dummyCout = Integer.parseInt(System.getenv("DUMMY_COUNT"));
		new File(dataDir + File.separator + "data_check").mkdir();
		List<String> dummyData = dummyDataCreator(dummyCout);
//		for (String string : dummyData) {
//			try {
//				mockMvc
//					.perform(post("/post-data").content(string).contentType(MediaType.APPLICATION_JSON))
//					.andDo(print())
//					.andExpect(status().isOk());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
		
	}
	
	@Test
	public void dataCheck() {
		if(!Files.exists(Paths.get(dataDir + File.separator + "data_check"))) {
			AppLogger.info("data_check missing run publishData test first");
			return;
		}
		Collection<String> fileNameSet = MappingConfigResolver.getMappingConfigVo().getTopicfileNameMapping().values();
		File file = new File(dataDir);
		if(file.isDirectory()) {
			for (String fileNameToBeChecked : fileNameSet) {
				try {
					File[] files = file.listFiles((dir, name) -> name.matches(fileNameToBeChecked.replace("_%d", "") + "_[0-9]+"));
					Arrays.sort(files, (f1, f2) -> Integer.parseInt(f1.getPath().split("_")[1]) - Integer.parseInt(f2.getPath().split("_")[1]));
					FileOutputStream out = new FileOutputStream(dataDir + File.separator + "data_check" + File.separator + fileNameToBeChecked.replace("_%d", "") + "_merged"); 
					for (int i = 0; i < files.length; i++) {
						Files.copy(files[0].toPath(), out);
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
		}else {
			AppLogger.info("configure dataDir in application.properties");
		}
	}
	
	private List<String> dummyDataCreator(int dataCount) throws IOException {
		
		Map<String, String> eventTopicMappingMap = MappingConfigResolver.getMappingConfigVo().getEventTopicMapping();
		Map<String, String> topicFileNameMap = MappingConfigResolver.getMappingConfigVo().getTopicfileNameMapping();
		Map<String, OutputStream> eventOutputMap = new HashMap<>();
		for(Entry<String, String> en:eventTopicMappingMap.entrySet()) {
				eventOutputMap.put(
					en.getKey(),
					new FileOutputStream(dataDir + File.separator + "data_check" 
								+ File.separator + topicFileNameMap.get(en.getValue()).replace("_%d", "") + "_published")
					
				);
		}
		
		
		ObjectMapper mapper = new ObjectMapper();
		List<String> dummyData = new ArrayList<>(); 
		Random rnd = new Random();
		String[] eventArray = new String[] {"clicks", "impressions"};
		for (int i = 0; i < dataCount; i++) {
			ObjectNode obj = mapper.createObjectNode();
			ObjectNode payload = mapper.createObjectNode();
			String eventType = eventArray[rnd.nextInt(2)];
			payload.put("event_type", eventType);
			payload.put("event_id", rnd.nextInt(10000));
			payload.put("timestamp", new Date().toString());
			payload.put("user_id", rnd.nextInt(10000));
			obj.set("payload", payload);
			dummyData.add(obj.toString());
			eventOutputMap.get(eventType).write(obj.toString().getBytes());
			eventOutputMap.get(eventType).write('\n');
		}
		return dummyData;
		
	}
	
	
	

}
