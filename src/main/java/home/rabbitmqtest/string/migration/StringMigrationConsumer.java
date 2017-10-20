package home.rabbitmqtest.string.migration;

import java.util.Random;

import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

import home.rabbitmqtest.config.AbstractMigrationConsumer;

public class StringMigrationConsumer extends AbstractMigrationConsumer<String, StringMigrationMessage> {

	public StringMigrationConsumer(String listeningQueueName, String failureQueueName, SimpleMessageListenerContainer failureMigrationContainer) {
		super(listeningQueueName, failureQueueName, failureMigrationContainer);
	}

	protected void processMessage(String payload) {
		boolean failed = new Random().nextBoolean();
		if (failed) {
			throw new RuntimeException("because the random");
		} else {
			System.out.println("string received: " + payload);
		}
	}
}
