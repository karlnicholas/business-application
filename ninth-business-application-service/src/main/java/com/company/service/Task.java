package com.company.service;

public class Task {
	public enum TASK_STATE {NONE, RESERVED, STARTED};
	private String comment;
	private TASK_STATE taskState;
	private boolean memberHr;
	

	public boolean isMemberHr() {
		return memberHr;
	}

	public void setMemberHr(boolean memberHr) {
		this.memberHr = memberHr;
	}

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
