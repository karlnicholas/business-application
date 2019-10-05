package hello;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class TaskController {
	private Task task;
	@ModelAttribute("task")
	public Task getTask() {
	    return task;
	}

	public TaskController() {
		task = new Task();
		task.setTaskState(Task.TASK_STATE.READY);
	}
	
	
	@RequestMapping({"/task"})
	public String showSeedstarters() {
	    return "task";
	}

	@RequestMapping(value="/task", method=RequestMethod.POST, params="action=starttask")
	public String starttask() {
		System.out.println("action=starttask");
		task.setTaskState(Task.TASK_STATE.STARTED);
	    return "redirect:/task";
	}

	@RequestMapping(value="/task", method=RequestMethod.POST, params="action=submitcomment")
	public String comment(@ModelAttribute Task task) {
		System.out.println("action=submitcomment");
		System.out.println("comment=" + task.getComment());
		this.task.setTaskState(Task.TASK_STATE.NONE);
	    return "redirect:/task";
	}
	
}
