package com.appdirect.migration.service.id;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import com.appdirect.migration.service.core.AbstractQueueMessageManager;
import com.appdirect.migration.service.core.PayloadMessage;
import com.appdirect.migration.service.client.RestClient;

public abstract class IdQueueMessageManager extends AbstractQueueMessageManager<Long> {
	private static final Logger log = LoggerFactory.getLogger(IdQueueMessageManager.class);
	@Autowired
	private RestClient restClient;

	public void processMessage(Long id) {
		log.debug("Processing message id:  <" + id + ">");
		restClient.invokeEndpointWithId(processOnePath(id), id);
	}

	@Override
	public void startMigration() {
		int totalPages;
		int pageIndex = 0;
		try {
			do {
				Page<Integer> page = restClient.fetchEntities(fetchAllPath(), pageIndex);
				page.forEach(id -> publishMessage(id.longValue()));
				totalPages = page.getTotalPages();
				pageIndex++;
			} while (pageIndex < totalPages);
		} catch (IOException e) {
			throw new RuntimeException("Could not start migration, due: ", e);
		}
	}

	@Override
	public Class getMessageClass() {
		return IdPayloadMessage.class;
	}

	@Override
	public PayloadMessage toPayloadMessage(Long id) {
		return new IdPayloadMessage(id);
	}

	protected abstract String fetchAllPath();

	protected abstract String processOnePath(Long id);

}
