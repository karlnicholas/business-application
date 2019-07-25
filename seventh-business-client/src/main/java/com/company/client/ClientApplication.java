package com.company.client;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.company.model.EmployeeEvaluation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class ClientApplication implements CommandLineRunner  {
	private static final Logger log = LoggerFactory.getLogger(ClientApplication.class);	
	private String containerId;
	private String serverBaseUrl;
	private String serverRestUrl;


    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

	@Override
	public void run(String... args) throws Exception {
        containerId = "Evaluation_1.0.0-SNAPSHOT";
        serverBaseUrl = "http://localhost:8090";
        serverRestUrl = serverBaseUrl + "/rest/server";
        runCustomApi();
        runjBPMClient();
        runjBPMApi();
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
        
        EmployeeEvaluation employeeEvaluation = new EmployeeEvaluation();
        employeeEvaluation.setEmployee("jack");

        ResponseEntity<Long> evaluation = 
    		restTemplate.exchange(serverBaseUrl + "/evaluation",
                HttpMethod.POST, new HttpEntity<EmployeeEvaluation>(employeeEvaluation, headersMary), Long.class);
        log.info("Started Process Instance: " + evaluation.getBody());

        employeeEvaluation.setSelfEval("Did lots of work");
		restTemplate.exchange(serverBaseUrl + "/selfeval",
            HttpMethod.POST, new HttpEntity<EmployeeEvaluation>(employeeEvaluation, headersJack), Long.class);

        employeeEvaluation.setHrEval("no incidents");
		restTemplate.exchange(serverBaseUrl + "/hreval",
            HttpMethod.POST, new HttpEntity<EmployeeEvaluation>(employeeEvaluation, headersMary), Long.class);

        employeeEvaluation.setHrEval("Projects Completed");
		restTemplate.exchange(serverBaseUrl + "/pmeval",
            HttpMethod.POST, new HttpEntity<EmployeeEvaluation>(employeeEvaluation, headersJohn), Long.class);

        ResponseEntity<List<VariableInstance>> instances = 
    		restTemplate.exchange(serverBaseUrl + "/instances?processInstanceId="+evaluation.getBody(),
                HttpMethod.GET, new HttpEntity<>(headersMary), new ParameterizedTypeReference<List<VariableInstance>>() {
			});

        for( VariableInstance variableInstance: instances.getBody() ) {
        	log.info(variableInstance.toString());
        }
	}

	private void runjBPMClient() throws JsonProcessingException {
		KieServicesClient clientMary = KieServicesFactory.newKieServicesClient(KieServicesFactory.newRestConfiguration(serverRestUrl, "mary", "mary").setExtraClasses(Collections.singleton(EmployeeEvaluation.class)));
		KieServicesClient clientJack = KieServicesFactory.newKieServicesClient(KieServicesFactory.newRestConfiguration(serverRestUrl, "jack", "jack").setExtraClasses(Collections.singleton(EmployeeEvaluation.class)));
		KieServicesClient clientJohn = KieServicesFactory.newKieServicesClient(KieServicesFactory.newRestConfiguration(serverRestUrl, "john", "john").setExtraClasses(Collections.singleton(EmployeeEvaluation.class)));

        ObjectMapper mapper = new ObjectMapper(); 
        EmployeeEvaluation employeeEvaluation = new EmployeeEvaluation();
        employeeEvaluation.setEmployee("jack");

		ProcessServicesClient processServices = clientMary.getServicesClient(ProcessServicesClient.class);
		Long processId = processServices.startProcess(containerId, "Evaluation.Evaluation", Collections.singletonMap("employeeEvaluation", employeeEvaluation));
		log.info("Started Process Instance: " + processId.toString());

        employeeEvaluation.setSelfEval("Did lots of work");
		performUserTask(clientJack, "jack", employeeEvaluation, false, mapper);
        employeeEvaluation.setHrEval("no incidents");
		performUserTask(clientMary, "mary", employeeEvaluation, true, mapper);
        employeeEvaluation.setPmEval("Projects Completed");
		performUserTask(clientJohn, "john", employeeEvaluation, true, mapper);

		List<VariableInstance> instances = processServices.findVariablesCurrentState(containerId, processId);
        for( VariableInstance variableInstance: instances ) {
        	log.info(variableInstance.toString());
        }
	}
	
	private void performUserTask(KieServicesClient client, String userId, EmployeeEvaluation employeeEvaluation, boolean claim, ObjectMapper mapper) throws JsonProcessingException {
		UserTaskServicesClient userTaskServices = client.getServicesClient(UserTaskServicesClient.class);
		List<TaskSummary> tasks = userTaskServices.findTasksAssignedAsPotentialOwner(userId, 0, 10);
        for ( TaskSummary taskSummary: tasks ) {
        	if ( claim ) {
    			userTaskServices.claimTask(containerId, taskSummary.getId(), userId);
        	}
			userTaskServices.startTask(containerId, taskSummary.getId(), userId);
    		userTaskServices.completeTask(containerId, taskSummary.getId(), userId, Collections.singletonMap("employeeEvaluation", employeeEvaluation));
        };
	}
	
	private void runjBPMApi() throws IOException {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
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

        ObjectMapper mapper = new ObjectMapper(); 

        EmployeeEvaluation employeeEvaluation = new EmployeeEvaluation();
        employeeEvaluation.setEmployee("jack");

        HttpEntity<String> requestEval = new HttpEntity<>(mapper.writeValueAsString(
        		Collections.singletonMap("employeeEvaluation", employeeEvaluation)), headersMary); 
        ResponseEntity<String> evaluation = 
    		restTemplate.exchange(serverRestUrl+"/containers/"+containerId+"/processes/Evaluation.Evaluation/instances",
                HttpMethod.POST, 
                requestEval, String.class );

        Long processId = Long.parseLong( evaluation.getBody() );
		log.info("Started Process Instance: " + processId.toString());
		

        employeeEvaluation.setSelfEval("Did lots of work");
        performUserTaskApi(restTemplate, headersJack, employeeEvaluation, false, mapper);
        employeeEvaluation.setPmEval("Projects Completed");
        performUserTaskApi(restTemplate, headersJohn, employeeEvaluation, true, mapper);
        employeeEvaluation.setHrEval("no incidents");
        performUserTaskApi(restTemplate, headersMary, employeeEvaluation, true, mapper);

		HttpEntity<String> requestVariables = new HttpEntity<>(headersMary);
		ResponseEntity<String> variables = 
			restTemplate.exchange(serverRestUrl+"/containers/"+containerId+"/processes/instances/"+processId+"/variables/instances",
                HttpMethod.GET, 
                requestVariables, String.class );
		JsonNode variableTree = mapper.readTree(variables.getBody());
		Iterator<JsonNode> variablesItr = variableTree.findValue("variable-instance").elements();
		while ( variablesItr.hasNext() ) {
			JsonNode node = variablesItr.next();
			log.info( node.toString() );
		}		
    }
	

	private void performUserTaskApi(RestTemplate restTemplate, HttpHeaders userHeaders, EmployeeEvaluation employeeEvaluation, boolean claim, ObjectMapper mapper) throws IOException {
		HttpEntity<String> emptyEntity = new HttpEntity<>(userHeaders);
		HttpEntity<String> paramsEntity = new HttpEntity<>(mapper.writeValueAsString(Collections.singletonMap("employeeEvaluation", employeeEvaluation)), userHeaders);
		ResponseEntity<String> potOwners = 
    		restTemplate.exchange(serverRestUrl+"/queries/tasks/instances/pot-owners",
                HttpMethod.GET, emptyEntity, String.class );

		JsonNode evalTree = mapper.readTree(potOwners.getBody());
        Long taskId = evalTree.findValue("task-id").asLong();

        if ( claim ) {
    		restTemplate.exchange(serverRestUrl+"/containers/"+containerId+"/tasks/"+taskId+"/states/claimed",
                HttpMethod.PUT, emptyEntity, String.class );
        }
		restTemplate.exchange(serverRestUrl+"/containers/"+containerId+"/tasks/"+taskId+"/states/started",
            HttpMethod.PUT, emptyEntity, String.class );

		restTemplate.exchange(serverRestUrl+"/containers/"+containerId+"/tasks/"+taskId+"/states/completed",
            HttpMethod.PUT, paramsEntity, String.class );

	}
 
 }