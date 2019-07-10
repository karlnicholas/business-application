package com.company.client;

import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class Application implements CommandLineRunner  {
	Logger log = LoggerFactory.getLogger(Application.class);	
	HttpHeaders headersMary;
	HttpHeaders headersJack;
	HttpHeaders headersJohn;
    RestTemplate restTemplate;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

	@Override
	public void run(String... args) throws Exception {
        restTemplate = new RestTemplate();
        createRequestEntities();
//        runCustomApi();
        runjBPMApi();
	}
	
	private void runCustomApi() {
        ResponseEntity<String> evaluation = 
        		restTemplate.exchange("http://localhost:8090/evaluation?employee=jack",
                        HttpMethod.GET, new HttpEntity<String>(headersMary), String.class);
        log.info(evaluation.toString());

        ResponseEntity<String> selfeval = 
        		restTemplate.exchange("http://localhost:8090/selfeval?selfeval=did+great",
                        HttpMethod.GET, new HttpEntity<String>(headersJack), String.class);
        log.info(selfeval.toString());

        ResponseEntity<String> hreval = 
        		restTemplate.exchange("http://localhost:8090/hreval?hreval=no+issues",
                        HttpMethod.GET, new HttpEntity<String>(headersMary), String.class);
        log.info(hreval.toString());

        ResponseEntity<String> pmeval = 
        		restTemplate.exchange("http://localhost:8090/pmeval?pmeval=projects+done",
                        HttpMethod.GET, new HttpEntity<String>(headersJohn), String.class);
        log.info(pmeval.toString());

        ResponseEntity<String> completed = 
        		restTemplate.exchange("http://localhost:8090/completed",
                        HttpMethod.GET, new HttpEntity<String>(headersMary), String.class);

        log.info(completed.toString());		
	}

	private void runjBPMApi() {
// curl -X POST "http://localhost:8080/kie-server/services/rest/server/containers/Evaluation_1.0.0-SNAPSHOT/processes/Evaluation.Evaluation/instances" -H "accept: application/json" -H "content-type: application/json" -d "{\"employee\": \"jack\"}"
/*		
		HttpEntity<String> requestEval = new HttpEntity<>("{\"employee\":\"jack\"}", headersMary); 
        ResponseEntity<String> evaluation = 
        		restTemplate.exchange("http://localhost:8090/rest/server/containers/Evaluation_1.0.0-SNAPSHOT/processes/Evaluation.Evaluation/instances",
                        HttpMethod.POST, 
                        requestEval, String.class );
        log.info(evaluation.toString());
*/        
/*
curl -X GET "http://localhost:8080/kie-server/services/rest/server/queries/tasks/instances/pot-owners?page=0&pageSize=10&sortOrder=true" -H "accept: application/json"
{
  "task-summary": [
    {
      "task-id": 4,
      "task-name": "Self Evaluation",
      "task-subject": "",
      "task-description": "",
      "task-status": "Reserved",
      "task-priority": 0,
      "task-is-skipable": false,
      "task-actual-owner": "jack",
      "task-created-by": "jack",
      "task-created-on": {
        "java.util.Date": 1562709707390
      },
      "task-activation-time": {
        "java.util.Date": 1562709707390
      },
      "task-expiration-time": null,
      "task-proc-inst-id": 2,
      "task-proc-def-id": "Evaluation.Evaluation",
      "task-container-id": "Evaluation_1.0.0-SNAPSHOT",
      "task-parent-id": -1
    }
  ]
}
http://localhost:8080/kie-server/services/rest/server/containers/Evaluation_1.0.0-SNAPSHOT/tasks/4/states/started
curl -X PUT "http://localhost:8080/kie-server/services/rest/server/containers/Evaluation_1.0.0-SNAPSHOT/tasks/4/states/completed" -H "accept: application/json" -H "content-type: application/json" -d "{\"selfeval\": \"did great work\"}"
 */
		HttpEntity<String> findSelfEval = new HttpEntity<>(headersJack);
		ResponseEntity<TaskSummaries> selfeval = 
        		restTemplate.exchange("http://localhost:8090/rest/server/queries/tasks/instances/pot-owners",
                        HttpMethod.GET, findSelfEval, TaskSummaries.class );
		TaskSummaries tss = selfeval.getBody();
        log.info(selfeval.toString());
        tss.getTaskSummaries().forEach(ts->System.out.println("Task: " + ts.getTaskId()));
        /*
GET http://localhost:8080/kie-server/services/rest/server/queries/tasks/instances/pot-owners?page=0&pageSize=10&sortOrder=true
PUT http://localhost:8080/kie-server/services/rest/server/containers/Evaluation_1.0.0-SNAPSHOT/tasks/5/states/claimed
PUT http://localhost:8080/kie-server/services/rest/server/containers/Evaluation_1.0.0-SNAPSHOT/tasks/5/states/started
PUT http://localhost:8080/kie-server/services/rest/server/containers/Evaluation_1.0.0-SNAPSHOT/tasks/5/states/completed
 */
        /*
        ResponseEntity<String> hreval = 
        		restTemplate.exchange("http://localhost:8090/hreval?hreval=no+issues",
                        HttpMethod.GET, requestMary, new ParameterizedTypeReference<String>() {
                });
        log.info(hreval.toString());
*/
/*
GET http://localhost:8080/kie-server/services/rest/server/queries/tasks/instances/pot-owners?page=0&pageSize=10&sortOrder=true
PUT http://localhost:8080/kie-server/services/rest/server/containers/Evaluation_1.0.0-SNAPSHOT/tasks/5/states/claimed
PUT http://localhost:8080/kie-server/services/rest/server/containers/Evaluation_1.0.0-SNAPSHOT/tasks/5/states/started
PUT http://localhost:8080/kie-server/services/rest/server/containers/Evaluation_1.0.0-SNAPSHOT/tasks/5/states/completed
*/
/*
        ResponseEntity<String> pmeval = 
        		restTemplate.exchange("http://localhost:8090/pmeval?pmeval=projects+done",
                        HttpMethod.GET, requestJohn, new ParameterizedTypeReference<String>() {
                });
        log.info(pmeval.toString());
*/        
/*
curl -X GET "http://localhost:8080/kie-server/services/rest/server/containers/Evaluation_1.0.0-SNAPSHOT/processes/instances/2/variables/instances" -H "accept: application/json"
curl -X GET "http://localhost:8080/kie-server/services/rest/server/queries/processes/instances/2/variables/instances" -H "accept: application/json"
*/
/*        
        ResponseEntity<String> completed = 
        		restTemplate.exchange("http://localhost:8090/completed",
                        HttpMethod.GET, requestMary, new ParameterizedTypeReference<String>() {
                });

        log.info(completed.toString());
*/        		
	}
	
	private void createRequestEntities() {
        byte[] base64CredsBytes = Base64.getEncoder().encode("mary:mary".getBytes());
        String maryCreds = new String(base64CredsBytes);
        headersMary = new HttpHeaders();
        headersMary.add("Authorization", "Basic " + maryCreds);        
        headersMary.add("accept", "application/json");        
        headersMary.add("content-type", "application/json");        

        base64CredsBytes = Base64.getEncoder().encode("jack:jack".getBytes());
        String jackCreds = new String(base64CredsBytes);
        headersJack = new HttpHeaders();
        headersJack.add("content-type", "application/json");
        headersJack.add("accept", "application/json");
        headersJack.add("Authorization", "Basic " + jackCreds);        

        base64CredsBytes = Base64.getEncoder().encode("john:john".getBytes());
        String johnCreds = new String(base64CredsBytes);
        headersJohn = new HttpHeaders();
        headersJohn.add("content-type", "application/json");        
        headersJohn.add("accept", "application/json");        
        headersJohn.add("Authorization", "Basic " + johnCreds);        
	}
 
 }