package com.appdirect.migration.service.core;

import java.io.Serializable;

public abstract class PayloadMessage<T> implements Serializable{
	private int failures = 0;
	private String cause;
	private T payload;

	public T getPayload() {
		return payload;
	}

	public PayloadMessage() {}

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

	public void setPayload(T payload) {
		this.payload = payload;
	}
}
