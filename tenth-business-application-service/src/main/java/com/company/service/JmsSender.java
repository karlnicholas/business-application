package com.company.service;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class JmsSender {
	
	private ActiveMQConnectionFactory activeMQConnectionFactory;

	public JmsSender() {
		activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL("vm://embedded-broker?broker.persistent=false");
	}

	public void sendMessage() {
		Destination destination;
		boolean transacted = false;
        
		try {
	
	        Connection connection = activeMQConnectionFactory.createConnection();
	        Session session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
	        destination = session.createQueue("helloworld.q");

	        MessageProducer producer = session.createProducer(destination);
	        
	        TextMessage message = session.createTextMessage();
	    
	        message.setText("Data");
	        producer.send(message);		
	        
	        producer.close();
	        session.close();
	        connection.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
