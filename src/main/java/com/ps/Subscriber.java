package com.ps;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class Subscriber implements Runnable, ExceptionListener {

    private ActiveMQConnectionFactory factory;

    public Subscriber(ActiveMQConnectionFactory factory) {
        this.factory = factory;
    }

    @Override
    public void run() {
        try {
            Connection connection = factory.createConnection();
            connection.setExceptionListener(this);
            connection.start();
            // not transacted, auto acknowledge
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // listen to a topic
            Topic topic = session.createTopic("topic.test");
            MessageConsumer topicConsumer = session.createConsumer(topic);
            topicConsumer.setMessageListener(message -> {
                try {
                    System.out.println("Received message from topic.test: " + ((TextMessage) message).getText());
                } catch (JMSException e) {
                    throw new RuntimeException(e);
                }
            });
            // listen to a queue
            Queue queue = session.createQueue("queue.test");
            MessageConsumer queueConsumer = session.createConsumer(queue);
            queueConsumer.setMessageListener(message -> {
                try {
                    System.out.println("Received message from queue.test: " + ((TextMessage) message).getText());
                } catch (JMSException e) {
                    throw new RuntimeException(e);
                }
            });
            Thread.sleep(10000);
            topicConsumer.close();
            queueConsumer.close();
            session.close();
            connection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onException(JMSException ex) {
        System.out.println("JMS Exception occured. Shutting down client.");
    }
}
