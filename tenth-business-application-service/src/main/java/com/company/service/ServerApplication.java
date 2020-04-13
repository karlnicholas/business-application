package com.company.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.mail.Address;
import javax.mail.internet.MimeMessage;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;
import org.kie.server.api.model.instance.VariableInstance;
import org.kie.server.api.model.instance.VariableInstanceList;
import org.kie.server.services.jbpm.ConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.model.EmployeeEvaluation;
import com.icegreen.greenmail.spring.GreenMailBean;
import com.icegreen.greenmail.util.GreenMail;

@SpringBootApplication(scanBasePackages = {"com.kendelong.jmxconsole.web.controller", "com.company.service"})
@RestController
@ImportAutoConfiguration(GreenMailBean.class)
public class ServerApplication implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(ServerApplication.class);
    @Autowired
    private ProcessService processService;
    @Autowired
    private RuntimeDataService runtimeDataService;
    @Autowired
    private UserTaskService userTaskService;

	@Autowired 
    private GreenMailBean greenMailBean;

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
 
	@PostMapping("/evaluation")
    public ResponseEntity<Long> startEvaluation(Principal principal, @RequestBody EmployeeEvaluation employeeEvaluation) throws Exception {
    	Long processInstanceId = -1L;
    	if ( principal != null ) {
	    	processInstanceId = processService.startProcess("Evaluation_1.0.0-SNAPSHOT", "Evaluation.Evaluation", Collections.singletonMap("employeeEvaluation", employeeEvaluation));
    	}
    	return ResponseEntity.ok(processInstanceId);
    }

    @PostMapping("/selfeval")
    public ResponseEntity<Integer> selfEvaluation(Principal principal, @RequestBody EmployeeEvaluation employeeEvaluation) throws Exception {
    	List<TaskSummary> taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner(principal.getName(), new QueryFilter());
    	taskSummaries.forEach(s->{
    		userTaskService.start(s.getId(), principal.getName());
    		userTaskService.complete(s.getId(), principal.getName(), Collections.singletonMap("employeeEvaluation", employeeEvaluation));
    	});
    	return ResponseEntity.ok(taskSummaries.size());
    }

    @PostMapping("/hreval")
    public ResponseEntity<Integer> hrEvaluation(Principal principal, @RequestBody EmployeeEvaluation employeeEvaluation) throws Exception {
    	List<TaskSummary> taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner(principal.getName(), new QueryFilter());
    	taskSummaries.forEach(s->{
    		userTaskService.claim(s.getId(), principal.getName());
    		userTaskService.start(s.getId(), principal.getName());
    		userTaskService.complete(s.getId(), principal.getName(), Collections.singletonMap("employeeEvaluation", employeeEvaluation));
    	});
    	return ResponseEntity.ok(taskSummaries.size());
    }

    @PostMapping("/pmeval")
    public ResponseEntity<Integer> pmEvaluation(Principal principal, @RequestBody EmployeeEvaluation employeeEvaluation) throws Exception {
    	List<TaskSummary> taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner(principal.getName(), new QueryFilter());
    	taskSummaries.forEach(s->{
    		userTaskService.claim(s.getId(), principal.getName());
    		userTaskService.start(s.getId(), principal.getName());
    		userTaskService.complete(s.getId(), principal.getName(), Collections.singletonMap("employeeEvaluation", employeeEvaluation));
    	});
    	return ResponseEntity.ok(taskSummaries.size());
    }

    @GetMapping("/instances")
    public ResponseEntity<List<VariableInstance>> instances(Principal principal, @RequestParam Long processInstanceId) throws Exception {
//    	List<ProcessInstanceWithVarsDesc> result = queryService.query("getVariablesCurrentState", ProcessInstanceWithVarsQueryMapper.get(), new QueryContext(), QueryParam.equalsTo(COLUMN_PROCESSINSTANCEID, 1L));
//    	System.out.println(result);
    	VariableInstanceList vi = ConvertUtils.convertToVariablesList(runtimeDataService.getVariablesCurrentState(processInstanceId));
    	return ResponseEntity.ok(Arrays.asList(vi.getVariableInstances()));
    }
    @GetMapping("/emails")
    public ResponseEntity<List<String>> instances() throws Exception {
    	GreenMail greenMail = greenMailBean.getGreenMail();
    	MimeMessage[] emails = greenMail.getReceivedMessages();
    	List<String> emailStrings = new ArrayList<>();
    	for ( MimeMessage email: emails ) {
    		StringBuilder sb = new StringBuilder();
    		for ( Address from: email.getFrom()) {
        		sb.append(" From["+from.toString()+"]");
    		}
    		for ( Address recip: email.getAllRecipients()) {
        		sb.append(" Recipient["+recip.toString()+"]");
    		}
    		sb.append(" Subject["+email.getSubject()+"]");
    		sb.append(" Content["+email.getContent()+"]");
    		emailStrings.add(sb.toString());
    	}
    	return ResponseEntity.ok(emailStrings);
    }
    @PostMapping("/datamodel")
    public ResponseEntity<Void> datamodelCallback(Principal principal, @RequestBody EmployeeEvaluation employeeEvaluation) throws Exception {
    	logger.info("Principal: {}", principal);
    	logger.info("EmployeeEvaluation: {}, {}, {}, {}", 
    			employeeEvaluation.getEmployee(), 
    			employeeEvaluation.getSelfEval(), 
    			employeeEvaluation.getPmEval(), 
    			employeeEvaluation.getHrEval()
    			);
    	return ResponseEntity.ok().build();
    }

    @Autowired
    Sender sender;
    
    @Autowired
    Receiver receiver;

    @Override
	public void run(String... args) throws Exception {
	      sender.send("Hello Spring JMS ActiveMQ!");

	      receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);	
	      
	      JmsSender jmsSender = new JmsSender();
	      jmsSender.sendMessage();
	}
}
