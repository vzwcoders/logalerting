package com.vzwcoders.localq;

import java.sql.Timestamp;
import java.util.Date;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import com.vzwcoders.dao.EventDAO;
import com.vzwcoders.jmx.JMXUtil;
import com.vzwcoders.util.StatsRunner;
import com.vzwcoders.vo.LogEvent;

public class MsgConsumer {

	private ConnectionFactory factory = null;
	private Connection connection = null;
	private Session session = null;
	private Destination destination = null;
	private MessageConsumer consumer = null;
	private int count=0;
	public static long CONSUMER_RECEIVE_MSG_COUNT;
	public static long MSG_INSERT_COUNT;
	public MsgConsumer() {

	}

	public void receiveMessage() {

		try {
			int count=0;
			while (true) {
				//System.out.println("Waiting for message");
				Message message = consumer.receive();
				MsgConsumer.CONSUMER_RECEIVE_MSG_COUNT++;
				try {
					JMXUtil.msgConsumerStatsmbean.incrReceiveMessageCount(1);
				} catch (Exception e1) {
					e1.printStackTrace();
					System.out.println("Error while incrementing msg count");
				}
				if (message instanceof TextMessage) {
					TextMessage text = (TextMessage) message;
					String m=text.getText();
					if(m==null || m.length()==0) continue;
						String k[]=m.split("#KEY#");
						if(k!=null && k.length!=2){
							System.out.println("Message format is not proper "+m);
							continue;
						}
					new EventDAO().insertEventLog(new LogEvent(count++,k[0] ,k[1], new Timestamp(new Date().getTime())));
						//System.out.println("inserted in to db");
						try {
							MsgConsumer.MSG_INSERT_COUNT++;
							JMXUtil.msgConsumerStatsmbean.incrDBInsertCount(1);
						} catch (Exception e) {
							e.printStackTrace();
							System.out.println("Error while incrementing db count");
						}
				}
			}

		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void init() throws JMSException {
		factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
		connection = factory.createConnection();
		connection = factory.createConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		destination = session.createQueue("logeventq");
		consumer = session.createConsumer(destination);
		EventDAO.init();
	}

	public static void main(String[] args) throws Exception {
		MsgConsumer receiver = new MsgConsumer();
		JMXUtil.initMsgConsumer();
		receiver.init();
		new StatsRunner().start();
		receiver.receiveMessage();
		
	}
}
