package home.rabbitmqtest.config;

public interface QueueMessageManager<T> {
	void processMessage(T payload);
	void publishMessage(T payload);
	String fromQueueName();
}
