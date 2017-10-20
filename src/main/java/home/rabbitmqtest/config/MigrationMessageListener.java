package home.rabbitmqtest.config;

import org.springframework.amqp.core.MessageListener;

public interface MigrationMessageListener extends MessageListener {
	String getListeningQueueName();
}
