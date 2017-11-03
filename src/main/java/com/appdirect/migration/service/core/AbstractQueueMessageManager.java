package com.appdirect.migration.service.core;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractQueueMessageManager<T> implements QueueMessageManager<T> {
	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Override
	public void publishMessage(T payload) {
		rabbitTemplate.convertAndSend(queueName(), toPayloadMessage(payload));
	}

	public abstract PayloadMessage toPayloadMessage(T payload);
}
