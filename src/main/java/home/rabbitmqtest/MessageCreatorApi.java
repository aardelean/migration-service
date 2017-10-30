package home.rabbitmqtest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import home.rabbitmqtest.id.migration.OrderQueueMessageManager;
import home.rabbitmqtest.string.migration.StringQueueMessageManager;

@Path("/create")
public class MessageCreatorApi {
	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private OrderQueueMessageManager orderQueueMessageManager;

	@Autowired
	private StringQueueMessageManager stringQueueMessageManager;

	@Autowired
	@Qualifier("migrateListener-long-failure")
	private SimpleMessageListenerContainer longFailureMigrationListener;

	@Autowired
	@Qualifier("migrateListener-string-failure")
	private SimpleMessageListenerContainer stringFailureMigrationListener;

	@GET
	@Path("/number")
	public String createNumberNotifications(@QueryParam("messages") Long noMessages) {
		for (long i=0; i < noMessages; i++) {
			orderQueueMessageManager.publishMessage(i);
		}
		return "Number notifications created!";
	}

	@GET
	@Path("/number/retryFailed")
	public String retryFailedNotifications() {
		longFailureMigrationListener.start();
		return "Retry triggered!";
	}

	@GET
	@Path("/text")
	public String createStringNotifications(@QueryParam("messages") Long noMessages) {
		for (int i=0; i < noMessages; i++) {
			stringQueueMessageManager.publishMessage(Integer.toString(i));
		}
		return "String notifications created!";
	}

	@GET
	@Path("/text/retryFailed")
	public String retryTextFailedNotifications() {
		stringFailureMigrationListener.start();
		return "Retry triggered!";
	}
}
