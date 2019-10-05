package hello;

public class Task {
	public enum TASK_STATE {NONE, READY, STARTED};
	private String comment;
	private TASK_STATE taskState;

	public TASK_STATE getTaskState() {
		return taskState;
	}

	public void setTaskState(TASK_STATE taskState) {
		this.taskState = taskState;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
