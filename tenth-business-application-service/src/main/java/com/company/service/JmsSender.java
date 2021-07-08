package com.company.service;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

public class JmsSender {

	public void sendMessage() {
		boolean transacted = false;
        
		try {
	
			Properties props = new Properties(); 
			props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory"); 
			props.setProperty(Context.PROVIDER_URL, "vm://embedded-broker?broker.persistent=false"); 
			javax.naming.Context jndiContext = new InitialContext(props);
			ConnectionFactory connectionFactory = (ConnectionFactory)jndiContext.lookup("ConnectionFactory"); 
			Destination destination = (Destination)jndiContext.lookup("dynamicQueues/helloworld.q"); 
			Connection connection = connectionFactory.createConnection();
	        Session session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
	        MessageProducer producer = session.createProducer(destination);
	        
	        TextMessage message = session.createTextMessage();
	    
	        message.setText("Data");
	        producer.send(message);		
	        
	        producer.close();
	        session.close();
	        connection.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
