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
package org.jbpm.process.workitem.jms;

import org.jbpm.process.workitem.core.AbstractLogOrThrowWorkItemHandler;
import org.jbpm.process.workitem.core.util.RequiredParameterValidator;
import org.jbpm.process.workitem.core.util.Wid;
import org.jbpm.process.workitem.core.util.WidMavenDepends;
import org.jbpm.process.workitem.core.util.WidParameter;
import org.jbpm.process.workitem.core.util.WidResult;
import org.jbpm.process.workitem.core.util.service.WidAction;
import org.jbpm.process.workitem.core.util.service.WidService;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.runtime.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jms.core.JmsTemplate;

@Wid(widfile = "JmsWorkItem.wid", name = "JmsPublishMessages",
        displayName = "JmsPublishMessages",
        defaultHandler = "mvel: new org.jbpm.process.workitem.jms.JmsWorkItemHandler()",
        documentation = "${artifactId}/index.html",
        category = "${artifactId}",
        icon = "JmsPublishMessages.png",
        parameters = {
                @WidParameter(name = "Message"), 
                @WidParameter(name = "QueueName")
        },
        results = {
                @WidResult(name = "Result")
        },
        mavenDepends = {
                @WidMavenDepends(group = "${groupId}", artifact = "${artifactId}", version = "${version}")
        },
        serviceInfo = @WidService(category = "${name}", description = "${description}",
                keywords = "jms,publish,message,topic",
                action = @WidAction(title = "Publish message to a jms topic")
        ))

public class JmsWorkItemHandler extends AbstractLogOrThrowWorkItemHandler implements Cacheable {

    private static final Logger logger = LoggerFactory.getLogger(JmsWorkItemHandler.class);
    
    private JmsTemplate jmsTemplate;
    
    public JmsWorkItemHandler(JmsTemplate jmsTemplate) {
    	this.jmsTemplate = jmsTemplate;
    }
    
    @Override
    public void executeWorkItem(
		WorkItem workItem, 
		WorkItemManager manager
    ) {
    	try {
	        RequiredParameterValidator.validate(this.getClass(),
	                workItem);
	
	        String message = (String) workItem.getParameter("Message");
	        String queueName = (String) workItem.getParameter("QueueName");
	        jmsTemplate.convertAndSend(queueName, message);
    
    	} catch (Exception e) {
            handleException(e);
        }
	}

    @Override
    public void abortWorkItem(WorkItem workItem,
                              WorkItemManager manager) {
    }

    @Override
    public void close() {
    }

}
