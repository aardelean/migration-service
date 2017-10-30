package home.rabbitmqtest.config;

import java.util.List;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;

public class MessageConsumer implements MessageListener {
	private static final int FAILURES_THRESHOLD = 2;
	private String failureQueuePrefix;

	@Autowired
	protected RabbitTemplate rabbitTemplate;
	@Autowired
	private List<QueueMessageManager> queueMessageManager;
	@Autowired
	private MessageConverter messageConverter;

	public MessageConsumer(String failureQueuePrefix) {
		this.failureQueuePrefix = failureQueuePrefix;
	}

	protected void failedMessage (PayloadMessage message, String failureQueueName) {
		message.setCause("because random");
		message.setFailures(message.getFailures() + 1);
		rabbitTemplate.convertAndSend(failureQueueName, message);
	}

	@Override
	public void onMessage(Message message) {
		String originatedQueue = message.getMessageProperties().getConsumerQueue();
		QueueMessageManager processor = getQueueMigrationMessageProcessor(originatedQueue);
		Object body = messageConverter.fromMessage(message);
		PayloadMessage payloadMessage = null;
		if (PayloadMessage.class.isAssignableFrom(body.getClass())) {
			payloadMessage = (PayloadMessage) body;
		} else {
			throw new IllegalArgumentException("different class expected from: " + body);
		}
		try {
			processor.processMessage(payloadMessage.getPayload());
		} catch (RuntimeException e) {
			if (payloadMessage.getFailures() >= FAILURES_THRESHOLD) {
				failedMessage(payloadMessage, originatedQueue + failureQueuePrefix);
			} else {
				payloadMessage.setFailures(payloadMessage.getFailures() +1);
				rabbitTemplate.convertAndSend(originatedQueue, payloadMessage);
			}
		}
	}

	public QueueMessageManager getQueueMigrationMessageProcessor(String messageQueue) {
		return queueMessageManager.stream()
				.filter(p -> p.fromQueueName().equals(messageQueue))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("no listener for the queue" + messageQueue));
	}
}
