package home.rabbitmqtest.id.migration;

import java.util.Random;

import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

import home.rabbitmqtest.config.AbstractMigrationConsumer;

public class LongMigrationConsumer extends AbstractMigrationConsumer<Long, LongMigrationMessage> {

	public LongMigrationConsumer(String listeningQueueName, String failureQueueName, SimpleMessageListenerContainer failureMigrationContainer) {
		super(listeningQueueName, failureQueueName, failureMigrationContainer);
	}

	@Override
	protected void processMessage(Long payload) {
		boolean failed = new Random().nextBoolean();
		if (failed) {
			throw new RuntimeException("because the random");
		} else {
			System.out.println("Received <" + payload + ">");
		}
	}
}
