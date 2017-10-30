package home.rabbitmqtest.id.migration;

import java.util.Random;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import home.rabbitmqtest.config.AbstractQueueMessageManager;
import home.rabbitmqtest.config.PayloadMessage;

@Component
@Slf4j
public class OrderQueueMessageManager extends AbstractQueueMessageManager<Long> {

	public void processMessage(Long orderId) {
		boolean failed = new Random().nextBoolean();
		if (failed) {
			throw new RuntimeException("because the random");
		} else {
			log.info("Received <" + orderId + ">");
		}
	}

	public String fromQueueName() {
		return "purchaseOrderMigration";
	}

	public PayloadMessage toPayloadMessage(Long payload) {
		return new OrderPayloadMessage(payload);
	}
}
