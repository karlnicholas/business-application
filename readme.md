### This repository contains one of the jBPM starter applications from [jBPM - Build your business application](https://start.jbpm.org/) expanded to demonstrate more complete examples.

The original-business-* contains the original starter business application. 
  * original-business-application-kjar: A kjar project. The kjar project holds the process flows, business rules, optimization and other information needed to implement and run in the jBPM runtime engine. The default starter kjar only has a default configuration files. Adding process flows will be covered later. The will build as is out of the box and installs with GAV of `com.company:business-application-kjar:1.0-SNAPSHOT`. The `pom.xml` that comes out of the box will cause issues with eclipse m2e and so an entry to fix the warnings from m2e has been added to the project's `pom.xml`.
  * original-business-application-model: A simple java project to be used as an external data model for business processes. The default contains an empty POJO at `com.company.model.Model`.
  * original-business-application-service: The default springboot jBPM service. The default server state is defined in `business-application-service.xml` in the project's root directory. This configuration file defines a deployed and running container with the same GAV value as the business-application-kjar. If you run the server it will initially fail with a java runtime exception `java.lang.RuntimeException: Cannot find KieModule: com.company:business-application-kjar:1.0-SNAPSHOT`. To fix this you must install the kjar into the local maven repository with `mvn install` from the kjar project. Alternatively the container startup information could be removed from the configuration file. Once the springboot jBPM service is started it is running on localhost port 8090. The jBPM REST endpoint can be found at http://localhost:8090/rest/server and is access restricted. The Authorization is configured in the DefaultWebSecurityConfig.java class of the project. 

### In order to create and test a simple script I created two projects by copying the respective `original-business-application-*` projects. 

  * first-business-application-kjar: 
  * first-business-application-service:  
 
The first step building a business process service will be to make a process flow. There are two obvious ways to do this. The first is to use the Business Central [Process Designer](https://docs.jboss.org/jbpm/release/7.22.0.Final/jbpm-docs/html_single/#_process_designer) and the second is to use the [Eclipse BPMN 2.0 Modeler](https://docs.jboss.org/jbpm/release/7.22.0.Final/jbpm-docs/html_single/#jBPMEclipseModeler).

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
 
 