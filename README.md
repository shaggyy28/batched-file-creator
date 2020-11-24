# BATCHED FILE CREATOR
This application listens on a webhook and injects the data recieved to the kafka topic, and the data from the kafka topic is dumped to the file depending upon the file size or time elapsed(which ever happens early).
### Configuration
###### application.properties file
This contents the configuration regarding the application, this file is given as command line arg
- batched.file.creator.time.threshold=30m  units - m/s
- batched.file.creator.size.threshold=5000KB  units - KB/MB
- batched.file.creator.data.dir=/file/path/to/backup/dir
###### mapping-config.json
this file contains the mapping for event, topic, fileName,
`flattened_data` flag signifies whether data is flattened or not for the corresponding topic
`filename_pattern` should contain one `%d`, which is replaced with fileNo
``` json
{
	"event_topic_mapping":[
		{
			"event_type": "clicks",
			"topic_name": "clicks_topic"
		},
		{
			"event_type": "impressions",
			"topic_name": "impressions_topic"
			
		}
	],
	"topic_filename_mapping": [
		{
			"topic_name": "clicks_topic",
			"filename_pattern": "clicks_%d", 
			"flattened_data": true
			
		},
		{
			"topic_name": "impressions_topic",
			"filename_pattern": "impressions_%d",
			"flattened_data": false
			
		}
	]
}
```
### Webhook
- Request_Url: http://localhost:8080/post-data
- Request-Type: POST 
- Content: json

`event_type` key denotes which topic data should be injected to.

`payload` object injected in the topic
``` json
    {
        "payload":{
            "event_type":"clicks",
            "event_id":5022,
            "timestamp":"Tue Oct 13 03:38:41 IST 2020",
            "user_id":1479
        }
    }
```
### Testing
clicks and impressions files with dummy data in `scripts` folder

`loadtest` script can be used to post data to webhook

__Usage__:
``` 
lodtest <file with dummy payloads>
```
`merge` script can be used to merge and check results.

__Usage__:
```
merge clicks,impressions
``` 
### Running Locally
- run `scripts/run_local` from root of the project, it will launch both kafka-consumer and web-api projects
- will use application-local.properties for config
- connect to kafka on localhost:9092 
- logs will redirected to .log file


### Dockerizing
`docker-compose.yml` file in project root contains the docker config to start 4 containers
- web-api (dockerfile in web-api subproject)
- kafka-batcher (dockerfile in kafka-batcher subproject)
- kafka (bitnami prebuilt image)
- zookeeper (bitnami prebuilt image)

will use application-docker.properties for config present in both sub-projects
`docker-compose up --build` starts the containers

