[Integrated jBPM](https://integratedjbpm.home.blog/)

### This repository contains one of the jBPM starter applications from [jBPM - Build your business application](https://start.jbpm.org/) expanded to demonstrate more complete examples.

The original-business-* contains the original starter business application. 
  * original-business-application-kjar: A kjar project. The kjar project holds the process flows, business rules, optimization and other information needed to implement and run in the jBPM runtime engine. The default starter kjar only has a default configuration files. Adding process flows will be covered later. The will build as is out of the box and installs with GAV of `com.company:business-application-kjar:1.0-SNAPSHOT`. The `pom.xml` that comes out of the box will cause issues with eclipse m2e and so an entry to fix the warnings from m2e has been added to the project's `pom.xml`.
  * original-business-application-model: A simple java project to be used as an external data model for business processes. The default contains an empty POJO at `com.company.model.Model`.
  * original-business-application-service: The default springboot jBPM service. The default server state is defined in `business-application-service.xml` in the project's root directory. This configuration file defines a deployed and running container with the same GAV value as the business-application-kjar. If you run the server it will initially fail with a java runtime exception `java.lang.RuntimeException: Cannot find KieModule: com.company:business-application-kjar:1.0-SNAPSHOT`. To fix this you must install the kjar into the local maven repository with `mvn install` from the kjar project. Alternatively the container startup information could be removed from the configuration file. Once the springboot jBPM service is started it is running on localhost port 8090. The jBPM REST endpoint can be found at http://localhost:8090/rest/server and is access restricted. The Authorization is configured in the DefaultWebSecurityConfig.java class of the project. 

### In order to create and test a simple script I created two projects by copying the respective `original-business-application-*` projects. 

  * first-business-application-kjar: 
  * first-business-application-service:  
 
The first step building a business process service will be to make a process flow. There are two obvious ways to do this. The first is to use the Business Central [Process Designer](https://docs.jboss.org/jbpm/release/7.22.0.Final/jbpm-docs/html_single/#_process_designer) and the second is to use the [Eclipse BPMN 2.0 Modeler](https://docs.jboss.org/jbpm/release/7.23.0.Final/jbpm-docs/html_single/#jBPMEclipseModeler).

Using the eclipse plugin would be very simple at this point but it has to be installed in Spring Tool Suite 4. The modeler can be found in the Eclipse Market place under [Eclipse BPMN2 Modeler](http://marketplace.eclipse.org/content/eclipse-bpmn2-modeler?mpc=true&mpc_state=) and so is easy to install.

Once installed a simple process flow is created by selecting src/main/resources in the first-business-application-kjar project and then file->new->other->BPMN->BPMN2 Model. In the dialog I selected the name of simple-process.bpmn2 and for Model Object I selected `process`. Unfortunately this created the process in the business-application main directory and I had to move it to the first-business-process-kjar/src/main/resources directory. I think right clicking on the resources directory would have created the simple process in the correct location.

The BPMN2 modeler should give a visual interface for process creation. Add a start event, a script task, and an end event, and connected them with arrows. When I first tried to open the process properties by opening the `properties` view and selecting the simple-process background I was not able to see all of the properties of a process that should be there. After closing and reopening the simple-process.bpmn2 file in Eclipse with the Bpmn2 Diagram Editor I was able to see all of the properties. Under the `Process` tab in the properties view I changed the process name to `SimpleProcess`. I then selected the script task and under the `Script Task` tab of the properties view I entered the script `System.out.println("Hello, World\n");`.

As of version 1.5.0 of the Eclipse BPMN2 Modeler there was no way to change the process ID under the Process Tab of properties. Open the simple-script.bpmn2 file in a text editor and change the processId by hand. The default was `<bpmn2:process id="Process_1"` and I changed it to `<bpmn2:process id="SimpleProcess"`. Process_1 was also found in another location in the file that needed to be changed as well.  

Compile the kjar in maven by selecting the first-business-application-kjar project and then run->run as->maven test. That workd with a few parser errors that I was not concerned about. I then updated the kjar in the local maven repository with run->run as->maven install. Be sure to remember that any change you make to the kjar needs to be followed with `maven install` because the kjar is loaded from the local repository.

Next is to invoke the process-flow from within the spring-boot service project. The service project is already configured to load the kjar and since the GAV value of `com.company:business-application-kjar:1.0-SNAPSHOT` has not changed the new business process with the SimpleProcess flow should still be loaded. The `com.company.service.Application.java` file is modified as follows:

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

Notice that the `org.jbpm.services.api.ProcessService` is injected into the application and used by the `run` method to start the process. Note also that the deploymentId argument is `"business-application-kjar-1_0-SNAPSHOT"`. This is set in the business-application-service.xml configuration file in the root of the service project.    

http://localhost:8090/rest/server/containers


### A next logical step to creating a Springboot based jBPM API is to create a simple `Hello, World` API. 

  * second-business-application-kjar: Holds a simple process flow that takes a "name" parameter and prepends a "Hello " on to the front of it in a script task.  
  * second-business-application-service: Adds a simple `RestController` to start the process flow, pass a name parameter to it, read and return the result.  
 
In order to manipulate a parameter in a business flow a process variable is added to the `simple-process.bpmn2` business process created in the `first-business-application-kjar` project.

The `simple-process.bpmn2` process is opened in the Eclipse BPMN2 modeler. The background of the process is clicked on (just to be sure) and the properties view is opened. The `Data Items` tab is opened selected and the plus `+` sign is clicked in the `Properties List for Process Simple Process` is clicked. A new process var is created with with the name `processVar1` and of `Data Type` `xs:string`. 

The `Script Task 1` is clicked and the `Script Task` tab is opened. The script in the `Script` field is changed to 

    kcontext.setVariable("processVar1", "Hello " + kcontext.getVariable("processVar1"));
    
In short, kcontext is available to all business process scripts and allows access to the jBPM API from within a business flow. This new script reads the process variable `processVar1` and prepends the string `Hello ` and stores the result back into the process variable.

Build and install the new kjar into the local maven repository.

Building the service rest interface is just a matter of changing the `Application.java` class. Remove the `CommandlineRunner` support and add `RestController` support. In order to read the result a new jBPM Service Interface is injected, the `RuntimeDataService`. The code is as follows:

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
	
The REST endpoint for this will be http://localhost:8090/hello and a `name` parameter is required. The final test URL could be [http://localhost:8090/hello?name=World](http://localhost:8090/hello?name=World). Invoking with endppoint with your browser will return the result `Hello World`.
 
 ### A Springboot application runs in docker very well and so runs in the cloud very well. 
 
 This capability is added by default to the jBPM sample applications.   

  * third-business-application-kjar: Same as second-business-application-kjar.  
  * third-business-application-service: pom.xml modified to point to ../third-business-application-kjar/target/ directory.
  
No modification is required to run the springboot-jBPM api under docker but the build process done by maven probably needs to be inspected and updated. 

The `third-business-application-service/pom.xml` file references the `local-repository` directory in the `third-business-application-kjar/target`. 

    <fileSet>
      <directory>../third-business-application-kjar/target/local-repository/maven</directory>
      <outputDirectory>opt/jboss/.m2/repository</outputDirectory>        
    </fileSet>
    
This is part of the `fabric8-maven-plugin` maven plugin which invokes docker to build the docker image. In order for the plugin to build the correct docker image the `<directory>` value must point to the `kjar` project. Note that there are two places in the `pom.xml` that has this setting. You can change them both -- one is for building a plain docker image and the other is for building an `openshift` docker image.

Building the docker image is a two step process. First the kjar `local-repository` must be built so that it can be included in the service project.

Build the `kjar` project first from the `third-business-application-kjar` project.

    mvn clean install -Ddocker
    
Notice that the docker profile is activated with a property setting instead of the usual maven -P setting. Once built there should be a directory `target/local-repository` in the `third-business-application-kjar` project.

Build the docker image from the `third-business-application-service` project. Be sure that you have an internet connection and docker is properly working on your system. The plugin will download the `fabric8/java-jboss-openjdk8-jdk` image for the OS and Java runtime.

    mvn clean install -Ddocker -Ph2
     
Now the docker image can be run.

    docker run -p 8080:8090 apps/business-application-service:1.0-SNAPSHOT
    
and the API can be tested with curl

    curl http://localhost:8080/hello?name=test
    

 ### A major step in jBPM process flows is interacting with users and groups. 
 
 As another general issue working with jBPM is the movement of business process projects in and out of kie-workbench.   

  * forth-business-central-kjar: Project copied from kie-workbench  
  * forth-business-application-service: Updated with endpoints and users and groups to handle human tasks.
  
The heart of the business process is human interactions. The process will be expanded to perform an employee evaluation which is a typical human interaction example in jBPM.

The biggest concern with jBPM and springboot at the moment is creating and managing the business process. The active development for the BPMN process builder is in the kie-workbench but the Eclipse BPMN plugin is closest to the project. The default project so far has been able to handle docker well but the Eclipse plugin builder has not been able to create a project with correct properties as seen earlier when the process id what not able to be set.

The kie-workbench projects can be copied into a java project by using GIT. Git is available when the kie-workbench is running by accessing port 8001.

A project is created in the kie-workbench. As stated it does an employee evaluation the same as the evaluation examples you will find in the jBPM source. There are four process variables, employee, selfeval, hreval, and pmeval. The employee is mapped as an input variable and is assigned to a that employee-user in the `Self Evaluation` task. The `HR Evaluation` and `PM Evaluation` are assigned to the groups `HR` and `PM` respectively. These must be claimed by members of those groups.

Running the project in the workbench allows each user to login and check for tasks in the `task inbox`. When the task is claimed, started, and completed a form is presented that allows the evaluation variables to be entered and saved. Once completed the variables can be inspected in kie-workbench and through the REST interface. Familiarity with creating, running, and managing business processes in kie-workbench is expected.

After building, deploying and testing on kie/business-central workbench the project is pulled with git.

  * A directory is created named forth-business-central-kjar
  * `git init` is run in the directory to initialize git 
  * `git add remote kie ssh://krisv@localhost:8001/MySpace/Evaluation` is run to create a connection to the kie/business-central workbench.
  * `git pull kie master` is run to pull the code from the workbench into the forth-business-central-kjar directory.
  
The project can be loaded into Eclipse and built with maven. The project will build properly as is with `mvn test` and the kjar can be installed into the local repository with `mvn install`.

After the new kjar is installed into the local maven repository with it can be deployed and started with the springboot application. The GAV for the new forth-business-central-kjar application is `com.myspace:Evaluation:1.0.0-SNAPSHOT` Remember that currently the deployment in the application is handled by the `business-application-service.xml` file. This will be changed to dreploy the new project.

	  <containers>
	    <container>
	      <containerId>Evaluation-1_0_0-SNAPSHOT</containerId>
	      <releaseId>
	        <groupId>com.myspace</groupId>
	        <artifactId>Evaluation</artifactId>
	        <version>1.0.0-SNAPSHOT</version>
	      </releaseId>
          ...
	  </containers>

The rest interface must be changed to start the evaluation process for a specific employee and then to complete the human tasks based on a `userId`. Since there are three different inputs to the evaluation process, `selfeval`, `hreval`, and `pmeval` there should be three different API endpoints for each stage of the evaluation process. In addition, appropriate users and roles must be added to the application so all human tasks can be claimed, started, and completed.

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("john").password("john").roles("PM");
        auth.inMemoryAuthentication().withUser("mary").password("mary").roles("HR");
        auth.inMemoryAuthentication().withUser("jack").password("jack").roles("user");
        auth.inMemoryAuthentication().withUser("user").password("user").roles("kie-server");
        auth.inMemoryAuthentication().withUser("wbadmin").password("wbadmin").roles("admin");
        auth.inMemoryAuthentication().withUser("kieserver").password("kieserver1!").roles("kie-server");
    }

and the new Application class:

	@SpringBootApplication
	@RestController
	public class Application  {	
	    @Autowired
	    private ProcessService processService;
	    @Autowired
	    private RuntimeDataService runtimeDataService;
	    @Autowired
	    private UserTaskService userTaskService;
	
	    public static void main(String[] args) {
	        SpringApplication.run(Application.class, args);
	    }
	 
	    @GetMapping("/evaluation")
	    public ResponseEntity<Long> startEvaluation(Principal principal, @RequestParam String employee) throws Exception {
	    	Long processInstanceId = -1L;
	    	if ( principal != null ) {
		    	Map<String, Object> vars = new HashMap<>();
		    	vars.put("employee", employee);
		    	processInstanceId = processService.startProcess("Evaluation-1_0_0-SNAPSHOT", "Evaluation.Evaluation", vars);
	    	}
	    	return ResponseEntity.ok(processInstanceId);
	    }
	
	    @GetMapping("/selfeval")
	    public ResponseEntity<Integer> selfEvaluation(Principal principal, @RequestParam String selfeval) throws Exception {
    		Map<String, Object> params = new HashMap<>();
    		params.put("selfeval", selfeval);
	    	List<TaskSummary> taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner(principal.getName(), new QueryFilter());
	    	taskSummaries.forEach(s->{
	    		userTaskService.start(s.getId(), principal.getName());
	    		userTaskService.complete(s.getId(), principal.getName(), params);
	    	});
	    	return ResponseEntity.ok(taskSummaries.size());
	    }

	    @GetMapping("/hreval")
	    public ResponseEntity<Integer> hrEvaluation(Principal principal, @RequestParam String hreval) throws Exception {
    		Map<String, Object> params = new HashMap<>();
    		params.put("hreval", hreval);
	    	List<TaskSummary> taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner(principal.getName(), new QueryFilter());
	    	taskSummaries.forEach(s->{
	    		userTaskService.claim(s.getId(), principal.getName());
	    		userTaskService.start(s.getId(), principal.getName());
	    		userTaskService.complete(s.getId(), principal.getName(), params);
	    	});
	    	return ResponseEntity.ok(taskSummaries.size());
	    }
	
	    @GetMapping("/pmeval")
	    public ResponseEntity<Integer> pmEvaluation(Principal principal, @RequestParam String pmeval) throws Exception {
    		Map<String, Object> params = new HashMap<>();
    		params.put("pmeval", pmeval);
	    	List<TaskSummary> taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner(principal.getName(), new QueryFilter());
	    	taskSummaries.forEach(s->{
	    		userTaskService.claim(s.getId(), principal.getName());
	    		userTaskService.start(s.getId(), principal.getName());
	    		userTaskService.complete(s.getId(), principal.getName(), params);
	    	});
	    	return ResponseEntity.ok(taskSummaries.size());
	    }
	}	

These new REST endpoints can be executed with authenticated HTTP requests

	curl -u mary:mary http://localhost:8090/evaluation?employee=jack
	52
	curl -u jack:jack "http://localhost:8090/selfeval?selfeval=test+selfeval"
	1
	curl -u john:john "http://localhost:8090/pmeval?pmeval=test+pmeval"
	1
	curl -u mary:mary "http://localhost:8090/hreval?hreval=test+hreval"
	1

Finally, completed evaluations should be retrievable along with their process variables. A new REST endpoint is made to access it.

    @GetMapping("/completed")
    public ResponseEntity<List<Collection<VariableDesc>>> completedEvaluations(Principal principal) throws Exception {
    	Collection<ProcessInstanceDesc> processInstances = runtimeDataService.getProcessInstances(Collections.singletonList(ProcessInstance.STATE_COMPLETED), principal.getName(), new QueryContext());
    	return ResponseEntity.ok(processInstances.stream()
			.map(pi->{return runtimeDataService.getVariablesCurrentState(pi.getId());})
			.collect(Collectors.toList())
		);
    }
	    
The new REST endpoint accessed with an authenticated http request:

    curl -u mary:mary "http://localhost:8090/completed"
    [[{"variableId":"employee","variableInstanceId":"employee","oldValue":"","newValue":"jack","deploymentId":"Evaluation-1_0_0-SNAPSHOT","processInstanceId":1,"dataTimeStamp":"2019-06-28T14:51:30.728+0000"},{"variableId":"initiator","variableInstanceId":"initiator","oldValue":"","newValue":"mary","deploymentId":"Evaluation-1_0_0-SNAPSHOT","processInstanceId":1,"dataTimeStamp":"2019-06-28T14:51:30.733+0000"},{"variableId":"selfeval","variableInstanceId":"selfeval","oldValue":"","newValue":"selfeval jack","deploymentId":"Evaluation-1_0_0-SNAPSHOT","processInstanceId":1,"dataTimeStamp":"2019-06-28T14:52:21.523+0000"},{"variableId":"pmeval","variableInstanceId":"pmeval","oldValue":"","newValue":"pmeval okay","deploymentId":"Evaluation-1_0_0-SNAPSHOT","processInstanceId":1,"dataTimeStamp":"2019-06-28T14:52:50.350+0000"},{"variableId":"hreval","variableInstanceId":"hreval","oldValue":"","newValue":"hreval okay","deploymentId":"Evaluation-1_0_0-SNAPSHOT","processInstanceId":1,"dataTimeStamp":"2019-06-28T14:53:11.290+0000"}]]

 ### Working with the server requires a client. 
 
 The springboot jBPM server also includes the jBPM REST interface so a client can be written either for the custom API created or the jBPM Rest API included. At this point it would be a matter of preference.    

  * fifth-business-central-kjar: Project copied from forth-business-central-kjar
  * fifth-business-application-service: Project copied from forth-business-application-service and one API endpoint updated for compatibility with spring `RestTemplate`. 
  * fifth-business-client: New project with client code to access jBPM workflow programatically. 
  
The next obvious step it write some client code to access the springboot jBPM service. Client code should start the business process and interact while simulatin various users. 

The process-flow in this example does an employee evaluation. Mary starts the evaluation process for Jack, Jack does his self evluation, and then Mary and John claim, start, and complete HR and PM evaluations respectively. When this is done the process variables are retrieved so the evaluation responses put in by the various users.

The point of the business process in this case is to both coordinate the various users and to obtain data from them. The bigger question this tutorial answers is about a business process creating informationn for an enterprise. A basic business process is about coordinating users but in the information age users are operating on digital data and not paper documents. jBPM use cases should be expanded to address this reality.

Here is the client code that was written for both the custom API and the server REST api:

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

