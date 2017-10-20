package home.rabbitmqtest.config;

import java.lang.reflect.ParameterizedType;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractMigrationConsumer<S, T extends AbstractMigrationMessage<S>> implements MigrationMessageListener {
	private static final int FAILURES_THRESHOLD = 2;

	private String listeningQueueName;
	private String failureQueueName;

	@Autowired
	protected RabbitTemplate rabbitTemplate;

	@Autowired
	private MessageConverter messageConverter;

	private SimpleMessageListenerContainer migrationFromFailureToUnprocessedListener;

	public AbstractMigrationConsumer(String listeningQueueName, String failureQueueName,
									 SimpleMessageListenerContainer migrationFromFailureToUnprocessedListener) {
		this.listeningQueueName = listeningQueueName;
		this.failureQueueName = failureQueueName;
		this.migrationFromFailureToUnprocessedListener = migrationFromFailureToUnprocessedListener;
	}

	protected abstract void processMessage(S payload);

	protected void failedMessage (T message) {
		message.setCause("because random");
		message.setFailures(message.getFailures() + 1);
		rabbitTemplate.convertAndSend(failureQueueName, message);
	}

	public void onMessage(Message message) {
		Object body = messageConverter.fromMessage(message);
		T migrationMessage  = null;
		Class<T> expectedMessageType = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
		if (body.getClass().isAssignableFrom(expectedMessageType)) {
			migrationMessage = (T)body;
		} else {
			throw new IllegalArgumentException("different class expected from: " + body);
		}
		try {
			processMessage(migrationMessage.getPayload());
		} catch (RuntimeException e) {
			if (migrationMessage.getFailures() >= getFailuresThreshold()) {
				failedMessage(migrationMessage);
			} else {
				migrationMessage.setFailures(migrationMessage.getFailures() +1);
				rabbitTemplate.convertAndSend(listeningQueueName, migrationMessage);
			}
		}
	}

	public void moveFromFailureToUnprocessedQueue() {
		migrationFromFailureToUnprocessedListener.start();
	}

	@Override
	public  String getListeningQueueName() {
		return listeningQueueName;
	}

	protected int getFailuresThreshold() {
		return FAILURES_THRESHOLD;
	}
}
