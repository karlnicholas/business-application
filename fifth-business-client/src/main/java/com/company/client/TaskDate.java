package com.company.client;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TaskDate {
	@JsonProperty("java.util.Date")
	private Date date;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
