package com.company.service;

import java.security.Principal;
import java.util.Collections;

import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@RestController
@EnableWebMvc
public class Application  {	
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    @Autowired
    private ProcessService processService;

	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }    
 
	@PostMapping("/evaluation")
    public ResponseEntity<Long> startEvaluation(Principal principal, @RequestBody String employee) throws Exception {
    	Long processInstanceId = -1L;
    	if ( principal != null ) {
	    	processInstanceId = processService.startProcess("Evaluation_1.0.0-SNAPSHOT", "Evaluation.Evaluation", Collections.singletonMap("employee", employee));
    	}
    	return ResponseEntity.ok(processInstanceId);
    }
}
