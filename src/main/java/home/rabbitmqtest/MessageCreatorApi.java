package home.rabbitmqtest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import home.rabbitmqtest.id.migration.LongMigrationConsumer;
import home.rabbitmqtest.id.migration.LongMigrationMessage;
import home.rabbitmqtest.string.migration.StringMigrationConsumer;
import home.rabbitmqtest.string.migration.StringMigrationMessage;

@Path("/create")
public class MessageCreatorApi {
	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private LongMigrationConsumer longMigrationConsumer;

	@Autowired
	private StringMigrationConsumer stringMigrationConsumer;

	@GET
	@Path("/number")
	public String createNumberNotifications(@QueryParam("messages") Long noMessages) {
		for (int i=0; i < noMessages; i++) {
			rabbitTemplate.convertAndSend(longMigrationConsumer.getListeningQueueName(), new LongMigrationMessage(new Long(i)));
		}
		return "Number notifications created!";
	}

	@GET
	@Path("/number/retryFailed")
	public String retryFailedNotifications() {
		longMigrationConsumer.moveFromFailureToUnprocessedQueue();
		return "Retry triggered!";
	}

	@GET
	@Path("/text")
	public String createStringNotifications(@QueryParam("messages") Long noMessages) {
		for (int i=0; i < noMessages; i++) {
			rabbitTemplate.convertAndSend(stringMigrationConsumer.getListeningQueueName(), new StringMigrationMessage(Integer.toString(i)));
		}
		return "String notifications created!";
	}

	@GET
	@Path("/text/retryFailed")
	public String retryTextFailedNotifications() {
		stringMigrationConsumer.moveFromFailureToUnprocessedQueue();
		return "Retry triggered!";
	}
}
