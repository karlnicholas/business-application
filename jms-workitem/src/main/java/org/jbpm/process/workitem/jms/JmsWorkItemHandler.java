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

import java.util.Map;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

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

@Wid(widfile = "JmsWorkItem.wid", name = "JmsPublishMessages",
        displayName = "JmsPublishMessages",
        defaultHandler = "mvel: new org.jbpm.process.workitem.jms.JmsWorkItemHandler()",
        documentation = "${artifactId}/index.html",
        category = "${artifactId}",
        icon = "JmsPublishMessages.png",
        parameters = {
                @WidParameter(name = "Message")
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
    
	private ConnectionFactory connectionFactory;
	private Destination destination;
    
    public JmsWorkItemHandler() {
    	try {
			Properties props = new Properties(); 
			props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory"); 
			props.setProperty(Context.PROVIDER_URL, "vm://embedded-broker?broker.persistent=false"); 
			javax.naming.Context jndiContext = new InitialContext(props);
			connectionFactory = (ConnectionFactory)jndiContext.lookup("ConnectionFactory"); 
			destination = (Destination)jndiContext.lookup("dynamicQueues/helloworld.q"); 
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
    }
    
    @Override
    public void executeWorkItem(
		WorkItem workItem, 
		WorkItemManager manager
    ) {
    	try {
	        RequiredParameterValidator.validate(this.getClass(),
	                workItem);

	        String message = (String) workItem.getParameter("message");
	
	        Connection connection = connectionFactory.createConnection();
	        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

	        MessageProducer producer = session.createProducer(destination);
	        
	        TextMessage textMessage = session.createTextMessage();
	    
	        textMessage.setText(message);
	        producer.send(textMessage);		
	        
	        producer.close();
	        session.close();
	        connection.close();
            
            manager.completeWorkItem(workItem.getId(), null);
            
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
