package com.company.client;

import java.util.Date;

import org.springframework.boot.jackson.JsonComponent;

import com.fasterxml.jackson.annotation.JsonProperty;

@JsonComponent
public class TaskSummary {
	@JsonProperty("task-id")
    private Long taskId;
	@JsonProperty("task-name")
    private String taskName;
	@JsonProperty("task-subjec")
    private String taskSubject;
	@JsonProperty("task-description")
    private String taskDescription;
	@JsonProperty("task-status")
    private String taskStatus;
	@JsonProperty("task-priority")
    private Integer taskPriority;
	@JsonProperty("task-is-skipable")
    private String taskIsSkipable;
	@JsonProperty("task-actual-owner")
    private String taskActualOwner;
	@JsonProperty("task-created-by")
    private String taskCreatedBy;
	@JsonProperty("task-created-on")
    private TaskDate taskCreatedOn;
	@JsonProperty("task-activation-time")
    private TaskDate taskActivationTime;
	@JsonProperty("task-expiration-time")
	private TaskDate taskExpirationTime;
	@JsonProperty("task-proc-inst-id")
    private Long taskProcInstId;
	@JsonProperty("task-proc-def-id")
    private String taskProcDefId;
	@JsonProperty("task-container-id")
    private String taskContainerId;
	@JsonProperty("task-parent-id")
    private Long taskParentId;
	public Long getTaskId() {
		return taskId;
	}
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getTaskSubject() {
		return taskSubject;
	}
	public void setTaskSubject(String taskSubject) {
		this.taskSubject = taskSubject;
	}
	public String getTaskDescription() {
		return taskDescription;
	}
	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}
	public String getTaskStatus() {
		return taskStatus;
	}
	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}
	public Integer getTaskPriority() {
		return taskPriority;
	}
	public void setTaskPriority(Integer taskPriority) {
		this.taskPriority = taskPriority;
	}
	public String getTaskIsSkipable() {
		return taskIsSkipable;
	}
	public void setTaskIsSkipable(String taskIsSkipable) {
		this.taskIsSkipable = taskIsSkipable;
	}
	public String getTaskActualOwner() {
		return taskActualOwner;
	}
	public void setTaskActualOwner(String taskActualOwner) {
		this.taskActualOwner = taskActualOwner;
	}
	public String getTaskCreatedBy() {
		return taskCreatedBy;
	}
	public void setTaskCreatedBy(String taskCreatedBy) {
		this.taskCreatedBy = taskCreatedBy;
	}
	public TaskDate getTaskCreatedOn() {
		return taskCreatedOn;
	}
	public void setTaskCreatedOn(TaskDate taskCreatedOn) {
		this.taskCreatedOn = taskCreatedOn;
	}
	public TaskDate getTaskActivationTime() {
		return taskActivationTime;
	}
	public void setTaskActivationTime(TaskDate taskActivationTime) {
		this.taskActivationTime = taskActivationTime;
	}
	public TaskDate getTaskExpirationTime() {
		return taskExpirationTime;
	}
	public void setTaskExpirationTime(TaskDate taskExpirationTime) {
		this.taskExpirationTime = taskExpirationTime;
	}
	public Long getTaskProcInstId() {
		return taskProcInstId;
	}
	public void setTaskProcInstId(Long taskProcInstId) {
		this.taskProcInstId = taskProcInstId;
	}
	public String getTaskProcDefId() {
		return taskProcDefId;
	}
	public void setTaskProcDefId(String taskProcDefId) {
		this.taskProcDefId = taskProcDefId;
	}
	public String getTaskContainerId() {
		return taskContainerId;
	}
	public void setTaskContainerId(String taskContainerId) {
		this.taskContainerId = taskContainerId;
	}
	public Long getTaskParentId() {
		return taskParentId;
	}
	public void setTaskParentId(Long taskParentId) {
		this.taskParentId = taskParentId;
	}
}
