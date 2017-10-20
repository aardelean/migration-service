package home.rabbitmqtest.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;

public class MigrateMessagesListener implements MigrationMessageListener {
	private String listeningQueueName;
	private String pushQueueName;

	@Autowired
	private MessageConverter messageConverter;
	@Autowired
	private RabbitTemplate rabbitTemplate;

	public MigrateMessagesListener(String listeningQueueName, String pushQueueName) {
		this.listeningQueueName = listeningQueueName;
		this.pushQueueName = pushQueueName;
	}

	@Override
	public void onMessage(Message message) {
		Object body = messageConverter.fromMessage(message);
		AbstractMigrationMessage migrationMessage  = null;
		if (AbstractMigrationMessage.class.isAssignableFrom(body.getClass())) {
			migrationMessage = (AbstractMigrationMessage)body;
		} else {
			throw new IllegalArgumentException("different class expected from: " + body);
		}
		migrationMessage.setCause(null);
		migrationMessage.setFailures(0);
		rabbitTemplate.convertAndSend(pushQueueName, migrationMessage);
	}

	public String getListeningQueueName() {
		return listeningQueueName;
	}
}
