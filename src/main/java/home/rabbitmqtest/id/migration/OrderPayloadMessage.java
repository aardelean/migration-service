package home.rabbitmqtest.id.migration;

import home.rabbitmqtest.config.PayloadMessage;

public class OrderPayloadMessage extends PayloadMessage<Long> {
	private Long payload;

	public OrderPayloadMessage(Long id) {
		this.payload = payload;
	}
	public OrderPayloadMessage() {}

	public Long getPayload() {
		return payload;
	}

	public void setPayload(Long id) {
		this.payload = payload;
	}
}
