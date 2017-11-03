package com.appdirect.migration.service.api;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.appdirect.migration.service.impl.order.OrderQueueMessageManager;

@Path("/trigger")
public class TriggerMessagesEndpoint {

	@Autowired
	private OrderQueueMessageManager orderQueueMessageManager;

	@Autowired
	@Qualifier("migrateListener-purchaseOrderMigration-failure")
	private SimpleMessageListenerContainer failureMessageListener;

	@GET
	@Path("/order")
	public void createNumberNotifications(@QueryParam("messages") Long noMessages) throws IOException {
		orderQueueMessageManager.startMigration();
	}

	@GET
	@Path("/order/retry")
	public String retryFailedNotifications() {
		failureMessageListener.start();
		return "Retry triggered!";
	}
}
