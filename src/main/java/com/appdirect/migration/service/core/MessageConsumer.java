package com.appdirect.migration.service.core;

import java.io.IOException;
import java.util.List;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

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
		message.setFailures(message.getFailures() + 1);
		rabbitTemplate.convertAndSend(failureQueueName, message);
	}

	@Override
	public void onMessage(Message message) {
		String originatedQueue = message.getMessageProperties().getConsumerQueue();
		QueueMessageManager processor = getQueueMigrationMessageProcessor(originatedQueue);
		PayloadMessage payloadMessage = getPayloadMessage(message, processor);

		try {
			processor.processMessage(payloadMessage.getPayload());
		} catch (RuntimeException e) {
			if (payloadMessage.getFailures() >= FAILURES_THRESHOLD) {
				payloadMessage.setCause(e.getMessage());
				failedMessage(payloadMessage, originatedQueue + failureQueuePrefix);
			} else {
				payloadMessage.setFailures(payloadMessage.getFailures() + 1);
				payloadMessage.setCause(e.getMessage());
				rabbitTemplate.convertAndSend(originatedQueue, payloadMessage);
			}
		}
	}

	private PayloadMessage getPayloadMessage(Message message, QueueMessageManager processor) {
		PayloadMessage payloadMessage = null;
		try {

			Object body = new ObjectMapper().readValue(message.getBody(), processor.getMessageClass());
			if (processor.getMessageClass().isAssignableFrom(body.getClass())) {
				payloadMessage = (PayloadMessage) body;
			} else {
				throw new IllegalArgumentException("Different class expected from: " + body);
			}
		} catch (IOException e) {
			throw new IllegalArgumentException("Could not deserialize body of message, ", e);
		}
		return payloadMessage;
	}

	public QueueMessageManager getQueueMigrationMessageProcessor(String messageQueue) {
		return queueMessageManager.stream()
				.filter(p -> p.queueName().equals(messageQueue))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("no listener for the queue" + messageQueue));
	}
}
