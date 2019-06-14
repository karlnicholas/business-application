package com.company.service;

import org.jbpm.services.api.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {
	
    @Autowired
    private ProcessService processService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
 
    @Override
    public void run(String... args) throws Exception {
    	processService.startProcess("business-application-kjar-1_0-SNAPSHOT", "SimpleProcess");
    }
}