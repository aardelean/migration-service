package com.appdirect.migration.service.core;

public interface QueueMessageManager<T> {
	void processMessage(T payload);
	void publishMessage(T payload);
	String queueName();
	void startMigration();
	Class getMessageClass();
}
