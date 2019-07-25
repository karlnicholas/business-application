package com.company.model;

import java.io.Serializable;

public class EmployeeEvaluation implements Serializable {
	private static final long serialVersionUID = 1L;
	private String employee;
	private String selfEval;
	private String hrEval;
	private String pmEval;
	public String getSelfEval() {
		return selfEval;
	}
	public void setSelfEval(String selfEval) {
		this.selfEval = selfEval;
	}
	public String getHrEval() {
		return hrEval;
	}
	public void setHrEval(String hrEval) {
		this.hrEval = hrEval;
	}
	public String getPmEval() {
		return pmEval;
	}
	public void setPmEval(String pmEval) {
		this.pmEval = pmEval;
	}
	public String getEmployee() {
		return employee;
	}
	public void setEmployee(String employee) {
		this.employee = employee;
	}
}