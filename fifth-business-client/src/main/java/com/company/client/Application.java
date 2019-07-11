package com.company.client;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kie.server.api.model.instance.TaskSummary;
import org.kie.server.api.model.instance.VariableInstance;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.UserTaskServicesClient;
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
	private static final Logger log = LoggerFactory.getLogger(Application.class);	
	private String containerId;
	private String serverBaseUrl;
	private String serverRestUrl;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

	@Override
	public void run(String... args) throws Exception {
        containerId = "Evaluation_1.0.0-SNAPSHOT";
        serverBaseUrl = "http://localhost:8080";
        serverRestUrl = serverBaseUrl + "/rest/server";
        runCustomApi();
        runjBPMClient();
	}
	
	private void runCustomApi() {
		RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type", "application/json");
        headers.add("accept", "application/json");

        HttpHeaders headersMary = new HttpHeaders();
        headersMary.addAll(headers);
        headersMary.add("Authorization", "Basic " + new String(Base64.getEncoder().encode("mary:mary".getBytes())));

        HttpHeaders headersJack = new HttpHeaders();
        headersJack.addAll(headers);
        headersJack.add("Authorization", "Basic " + new String(Base64.getEncoder().encode("jack:jack".getBytes())));        

        HttpHeaders headersJohn = new HttpHeaders();
        headersJohn.addAll(headers);
        headersJohn.add("Authorization", "Basic " + new String(Base64.getEncoder().encode("john:john".getBytes())));        

        ResponseEntity<Long> evaluation = 
        		restTemplate.exchange(serverBaseUrl + "/evaluation?employee=jack",
                        HttpMethod.GET, new HttpEntity<Void>(headersMary), Long.class);
        log.info("Started Process Instance: " + evaluation.getBody());

		restTemplate.exchange(serverBaseUrl + "/selfeval?selfeval=did+great",
                HttpMethod.GET, new HttpEntity<>(headersJack), Long.class);

		restTemplate.exchange(serverBaseUrl + "/hreval?hreval=no+issues",
                HttpMethod.GET, new HttpEntity<>(headersMary), Long.class);

		restTemplate.exchange(serverBaseUrl + "/pmeval?pmeval=projects+done",
                HttpMethod.GET, new HttpEntity<>(headersJohn), Long.class);

        ResponseEntity<List<VariableInstance>> instances = 
        		restTemplate.exchange(serverBaseUrl + "/instances?processInstanceId="+evaluation.getBody(),
                        HttpMethod.GET, new HttpEntity<>(headersMary), new ParameterizedTypeReference<List<VariableInstance>>() {
						});

        for( VariableInstance variableInstance: instances.getBody() ) {
        	log.info(variableInstance.toString());
        }
	}

	private void runjBPMClient() {
		KieServicesClient clientMary = KieServicesFactory.newKieServicesClient(KieServicesFactory.newRestConfiguration(serverRestUrl, "mary", "mary"));
		KieServicesClient clientJack = KieServicesFactory.newKieServicesClient(KieServicesFactory.newRestConfiguration(serverRestUrl, "jack", "jack"));
		KieServicesClient clientJohn = KieServicesFactory.newKieServicesClient(KieServicesFactory.newRestConfiguration(serverRestUrl, "john", "john"));

		ProcessServicesClient processServices = clientMary.getServicesClient(ProcessServicesClient.class);
		Long processId = processServices.startProcess(containerId, "Evaluation.Evaluation", Collections.singletonMap("employee", "jack"));
		log.info("Started Process Instance: " + processId.toString());

		performUserTask(clientJack, "jack", Collections.singletonMap("selfeval", "did lots of work"), false);
		performUserTask(clientMary, "mary", Collections.singletonMap("hreval", "no incidents"), true);
		performUserTask(clientJohn, "john", Collections.singletonMap("pmeval", "projects completed"), true);

		List<VariableInstance> instances = processServices.findVariablesCurrentState(containerId, processId);
        for( VariableInstance variableInstance: instances ) {
        	log.info(variableInstance.toString());
        }
	}
	
	private void performUserTask(KieServicesClient client, String userId, Map<String, Object> params, boolean claim) {
		UserTaskServicesClient userTaskServices = client.getServicesClient(UserTaskServicesClient.class);
		List<TaskSummary> tasks = userTaskServices.findTasksAssignedAsPotentialOwner(userId, 0, 10);
        for ( TaskSummary taskSummary: tasks ) {
        	if ( claim ) {
    			userTaskServices.claimTask(containerId, taskSummary.getId(), userId);
        	}
			userTaskServices.startTask(containerId, taskSummary.getId(), userId);
    		userTaskServices.completeTask(containerId, taskSummary.getId(), userId, params);
        };
		
	}

 }