package home.rabbitmqtest.config;

import java.io.Serializable;

public abstract class AbstractMigrationMessage<T> implements Serializable{
	private T payload;
	private int failures = 0;
	private String cause;

	public AbstractMigrationMessage(T payload) {
		this.payload = payload;
	}

	public AbstractMigrationMessage() {}

	public T getPayload() {
		return payload;
	}

	public int getFailures() {
		return failures;
	}

	public void setFailures(int failures) {
		this.failures = failures;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}
}
