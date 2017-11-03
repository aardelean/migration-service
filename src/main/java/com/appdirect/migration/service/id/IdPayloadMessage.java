package com.appdirect.migration.service.id;

import com.appdirect.migration.service.core.PayloadMessage;

public class IdPayloadMessage extends PayloadMessage<Long> {
	private Long payload;

	public IdPayloadMessage(Long id) {
		this.payload = id;
	}
	public IdPayloadMessage() {}

	public Long getPayload() {
		return payload;
	}

	public void setPayload(Long id) {
		this.payload = id;
	}
}
