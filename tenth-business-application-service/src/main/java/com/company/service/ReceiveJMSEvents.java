package com.company.service;

import javax.jms.BytesMessage;

import org.jbpm.process.workitem.jms.JMSSignalReceiver;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class ReceiveJMSEvents extends JMSSignalReceiver {

    @JmsListener(destination = "ExternalSignalQueue")
    public void processMessage(BytesMessage content) {
        super.onMessage(content);
    }

}