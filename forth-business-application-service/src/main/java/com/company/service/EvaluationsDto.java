package com.company.service;

import java.util.Collection;

import org.jbpm.services.api.model.VariableDesc;

public class EvaluationsDto {
	private String initiator, employee, selfeval, pmeval, hreval;
	public EvaluationsDto(Collection<VariableDesc> variablesCurrentState) {
		variablesCurrentState.forEach(v->{
			switch( v.getVariableId()) {
			case "initiator":
				initiator = v.getNewValue();
				break;
			case "employee":
				employee = v.getNewValue();
				break;
			case "selfeval":
				selfeval = v.getNewValue();
				break;
			case "hreval":
				hreval = v.getNewValue();
				break;
			case "pmeval":
				pmeval = v.getNewValue();
				break;
			}
		});
	}
	public String getInitiator() {
		return initiator;
	}
	public String getEmployee() {
		return employee;
	}
	public String getSelfeval() {
		return selfeval;
	}
	public String getPmeval() {
		return pmeval;
	}
	public String getHreval() {
		return hreval;
	}
}
