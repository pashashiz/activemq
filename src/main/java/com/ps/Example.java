package com.ps;

import org.apache.activemq.ActiveMQConnectionFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Example {

    public static void main(String[] args) throws InterruptedException {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(new Subscriber(connectionFactory));
        executor.execute(new Subscriber(connectionFactory));
        Thread.sleep(1000);
        executor.execute(new Publisher(connectionFactory));
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.MINUTES);
    }
}
