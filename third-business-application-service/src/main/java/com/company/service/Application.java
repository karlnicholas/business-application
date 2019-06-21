package com.company.service;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.VariableDesc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application  {
	
    @Autowired
    private ProcessService processService;
    @Autowired
    private RuntimeDataService runtimeDataService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
 
    @GetMapping("/hello")
    public ResponseEntity<String> sayHello(@RequestParam String name) throws Exception {
    	Map<String, Object> vars = new HashMap<>();
    	vars.put("processVar1", name);
    	Long processInstanceId = processService.startProcess("business-application-kjar-1_0-SNAPSHOT", "SimpleProcess", vars);
    	for ( VariableDesc var: runtimeDataService.getVariablesCurrentState(processInstanceId) ) {
    		if ( var.getVariableId().equals("processVar1"))
    	    	return ResponseEntity.ok(var.getNewValue());
    	}
    	return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body("Processing Error");
    }
}