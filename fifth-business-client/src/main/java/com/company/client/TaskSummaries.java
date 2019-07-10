package com.company.client;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TaskSummaries {
	@JsonProperty("task-summary")
	private List<TaskSummary> taskSummaries;

	public List<TaskSummary> getTaskSummaries() {
		return taskSummaries;
	}

	public void setTaskSummaries(List<TaskSummary> taskSummaries) {
		this.taskSummaries = taskSummaries;
	} 
}
