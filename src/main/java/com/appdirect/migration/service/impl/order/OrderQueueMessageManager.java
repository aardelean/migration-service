package com.appdirect.migration.service.impl.order;

import org.springframework.stereotype.Component;

import com.appdirect.migration.service.id.IdQueueMessageManager;

@Component
public class OrderQueueMessageManager extends IdQueueMessageManager {

	public String queueName() {
		return "purchaseOrderMigration";
	}

	//path to appdirect resource to obtain all information
	protected String fetchAllPath() {
		return "/v1/migration/orders";
	}

	//path to appdirect resource migrate the order with this id
	protected String processOnePath(Long id) {
		return fetchAllPath() + "/" + id;
	}
}
