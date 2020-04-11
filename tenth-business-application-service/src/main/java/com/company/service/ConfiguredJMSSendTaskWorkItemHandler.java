package com.company.service;

import javax.jms.ConnectionFactory;

import org.jbpm.process.workitem.jms.JMSSendTaskWorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component("External Send Task")
public class ConfiguredJMSSendTaskWorkItemHandler extends JMSSendTaskWorkItemHandler {

    private JmsTemplate jmsTemplate;

    public ConfiguredJMSSendTaskWorkItemHandler(ConnectionFactory connectionFactory, JmsTemplate jmsTemplate) {
        super(connectionFactory, null);
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        try {
            jmsTemplate.send("ExternalSignalQueue", (session) -> createMessage(workItem, session));
            manager.completeWorkItem(workItem.getId(), null);
        } catch (Exception e) {
            handleException(e);
        }
    }

}