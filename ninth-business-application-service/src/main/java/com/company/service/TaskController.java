package com.company.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;
import org.kie.server.api.model.instance.VariableInstance;
import org.kie.server.api.model.instance.VariableInstanceList;
import org.kie.server.services.jbpm.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.company.service.Task.TASK_STATE;

@Controller
public class TaskController {
    private Task task;
	@ModelAttribute("task")
	public Task getTask() {
	    return task;
	}

    @Autowired
    private ProcessService processService;
    @Autowired
    private RuntimeDataService runtimeDataService;
    @Autowired
    private UserTaskService userTaskService;
    
    public TaskController() {
		task = new Task();
    }
    
	@RequestMapping({"/task"})
	public String showTasks(Principal principal) {
		task.setComment(null);
    	if ( principal != null ) {
        	List<TaskSummary> taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner(principal.getName(), new QueryFilter());
        	if ( taskSummaries.size() > 0 ) {
            	Status status = taskSummaries.get(0).getStatus();
            	if ( status == Status.Reserved || status == Status.Ready )
            		task.setTaskState(TASK_STATE.RESERVED);
            	else if ( status == Status.InProgress )
            		task.setTaskState(TASK_STATE.STARTED);
        	} else {
        		task.setTaskState(TASK_STATE.NONE);
        	}
        	task.setMemberHr(hasRole("ROLE_HR"));
        	if ( task.isMemberHr() ) {
        		Collection<ProcessInstanceDesc> piDescs = runtimeDataService.getProcessInstances(new QueryContext());
        		List<Eval> evals = new ArrayList<>();
        		task.setEvals(evals);
        		for ( ProcessInstanceDesc piDesc: piDescs ) {
                	VariableInstanceList vil = ConvertUtils.convertToVariablesList(runtimeDataService.getVariablesCurrentState(piDesc.getId()));
                	Eval eval = new Eval();
                	evals.add(eval);
					for ( VariableInstance vi : vil.getItems() ) {
						switch( vi.getVariableName()) {
						case "employee":
							eval.setEmployee(vi.getValue());
							break;
						case "selfeval":
							eval.setSelfeval(vi.getValue());
							break;
						case "pmeval":
							eval.setPmeval(vi.getValue());
							break;
						case "hreval":
							eval.setHreval(vi.getValue());
							break;
						}
                	}
        		}
        	}
    	}
	    return "task";
	}

	@RequestMapping(value="/task", method=RequestMethod.POST, params="action=startevaluation")
    public String startEvaluation(Principal principal, @ModelAttribute Task task) throws Exception {
    	if ( principal != null ) {
	    	Map<String, Object> vars = new HashMap<>();
	    	vars.put("employee", task.getComment());
	    	processService.startProcess("Evaluation_1.0.0-SNAPSHOT", "Evaluation.Evaluation", vars);
    	}
    	return "redirect:/task";
    }

	@RequestMapping(value="/task", method=RequestMethod.POST, params="action=completeevaluation")
    public String selfEvaluation(Principal principal, @ModelAttribute Task task) throws Exception {
    	List<TaskSummary> taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner(principal.getName(), new QueryFilter());
		Map<String, Object> params = setParams(taskSummaries, task);
    	taskSummaries.forEach(s->{
        	Status status = taskSummaries.get(0).getStatus();
        	if ( status == Status.Ready )
        		userTaskService.claim(s.getId(), principal.getName());
    		userTaskService.start(s.getId(), principal.getName());
    		userTaskService.complete(s.getId(), principal.getName(), params);
    	});
    	return "redirect:/task";
    }

	private Map<String, Object> setParams(List<TaskSummary> taskSummaries, Task task) {
		Map<String, Object> params = new HashMap<>();
		if ( taskSummaries.size() > 0 ) {
			String taskName = taskSummaries.get(0).getName().toLowerCase();
			if ( taskName.contains("self") ) 
				params.put("selfeval", task.getComment());
			else if ( taskName.contains("hr") ) 
				params.put("hreval", task.getComment());
			else if ( taskName.contains("pm") ) 
				params.put("pmeval", task.getComment());
		}
		return params;
	}

	private boolean hasRole(String roleName)
    {
        return SecurityContextHolder
        		.getContext()
        		.getAuthentication()
        		.getAuthorities()
        		.stream()
                .anyMatch(grantedAuthority->{
                	System.out.println("Role: " + grantedAuthority.getAuthority());
                	return grantedAuthority.getAuthority().equals(roleName);	
                });
    }
/*	
	@RequestMapping(value="/task", method=RequestMethod.POST, params="action=hrevaluation")
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

	@RequestMapping(value="/task", method=RequestMethod.POST, params="action=pmevaluation")
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
*/
}
