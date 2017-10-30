package home.rabbitmqtest.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractQueueMessageManager<T> implements QueueMessageManager<T> {
	@Autowired
	private RabbitTemplate rabbitTemplate;

	public void publishMessage(T payload) {
		rabbitTemplate.convertAndSend(fromQueueName(), toPayloadMessage(payload));
	}

	public abstract PayloadMessage toPayloadMessage(T payload);
}
