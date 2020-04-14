/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.company.service;

import org.jbpm.process.workitem.core.AbstractLogOrThrowWorkItemHandler;
import org.jbpm.process.workitem.core.util.RequiredParameterValidator;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;


@Component("JmsPublishMessages")
public class JmsPublishMessages extends AbstractLogOrThrowWorkItemHandler {
    
	  @Autowired
	  private JmsTemplate jmsTemplate;
    
    @Override
    public void executeWorkItem(
		WorkItem workItem, 
		WorkItemManager manager
    ) {
    	try {
	        RequiredParameterValidator.validate(this.getClass(), workItem);

	        String message = (String) workItem.getParameter("Message");
	        jmsTemplate.convertAndSend("helloworld.q", message);
            
            manager.completeWorkItem(workItem.getId(), null);
            
    	} catch (Exception e) {
            handleException(e);
        }
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// TODO Auto-generated method stub
		
	}

}
