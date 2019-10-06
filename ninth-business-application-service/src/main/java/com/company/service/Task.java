package com.company.service;

import java.util.List;

public class Task {
	public enum TASK_STATE {NONE, RESERVED, STARTED};
	private String comment;
	private TASK_STATE taskState;
	private boolean memberHr;
	private List<Eval> evals;

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

	public List<Eval> getEvals() {
		return evals;
	}

	public void setEvals(List<Eval> evals) {
		this.evals = evals;
	}

}
