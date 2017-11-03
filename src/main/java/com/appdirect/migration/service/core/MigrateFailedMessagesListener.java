package com.appdirect.migration.service.core;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;

public class MigrateFailedMessagesListener implements MessageListener {

	private String failureSuffix;
	@Autowired
	private MessageConverter messageConverter;
	@Autowired
	private RabbitTemplate rabbitTemplate;

	public MigrateFailedMessagesListener(String failureSuffix) {
		this.failureSuffix = failureSuffix;
	}

	@Override
	public void onMessage(Message message) {
		Object body = messageConverter.fromMessage(message);
		PayloadMessage payloadMessage = null;
		if (PayloadMessage.class.isAssignableFrom(body.getClass())) {
			payloadMessage = (PayloadMessage)body;
		} else {
			throw new IllegalArgumentException("Different class expected from: " + body);
		}
		payloadMessage.setCause(null);
		payloadMessage.setFailures(0);
		String originalQueueName = message.getMessageProperties().getConsumerQueue().substring(0, message.getMessageProperties().getConsumerQueue().indexOf(failureSuffix));
		rabbitTemplate.convertAndSend(originalQueueName, payloadMessage);
	}
}
