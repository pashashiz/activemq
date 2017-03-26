package com.ps;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class Publisher implements Runnable, ExceptionListener {

    private ActiveMQConnectionFactory factory;

    public Publisher(ActiveMQConnectionFactory factory) {
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
            // send to a topic
            Topic topic = session.createTopic("topic.test");
            MessageProducer topicProducer = session.createProducer(topic);
            topicProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            topicProducer.send(topic, session.createTextMessage("From topic on [" + threadName() + "] thread"));
            // send to a queue
            Queue queue = session.createQueue("queue.test");
            MessageProducer queueProducer = session.createProducer(queue);
            queueProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            queueProducer.send(queue, session.createTextMessage("From queue on [" + threadName() + "] thread"));
            session.close();
            connection.close();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    private String threadName() {
        return Thread.currentThread().getName();
    }

    @Override
    public synchronized void onException(JMSException ex) {
        System.out.println("JMS Exception occured. Shutting down client.");
    }
}
