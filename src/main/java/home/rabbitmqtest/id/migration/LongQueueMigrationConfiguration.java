package home.rabbitmqtest.id.migration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import home.rabbitmqtest.config.MigrateMessagesListener;
import home.rabbitmqtest.config.QueueConfiguration;

@Configuration
public class LongQueueMigrationConfiguration {

	@Autowired
	private QueueConfiguration.ContainerBuilder containerBuilder;

	@Bean(name = "longQueue")
	Queue queue() {
		return new Queue(getQueueName(), true);
	}

	@Bean(name = "longQueueFailure")
	Queue failedQueue() {
		return new Queue(getFailureQueueName(), true);
	}

	@Bean
	Binding bindingLong(@Qualifier("longQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(getQueueName());
	}

	@Bean
	Binding bindingLongFailureQueue(@Qualifier("longQueueFailure") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(getFailureQueueName());
	}

	@Bean
	public LongMigrationConsumer longMigrationConsumer(@Qualifier("longRetryFailureContainer") SimpleMessageListenerContainer failureMigrationContainer) {
		return new LongMigrationConsumer(getQueueName(), getFailureQueueName(), failureMigrationContainer);
	}

	@Bean
	SimpleMessageListenerContainer containerLongMigration(LongMigrationConsumer consumer) {
		return containerBuilder.buildSimpleMessageListenerContainer(consumer);
	}

	@Bean(name = "longRetryFailureContainer")
	SimpleMessageListenerContainer containerLongRetryFailureMigration(@Qualifier("longFailureMigrationListener") MigrateMessagesListener migrateMessagesListener) {
		return containerBuilder.buildFailureMigrationListenerContainer(migrateMessagesListener);
	}

	@Bean(name = "longFailureMigrationListener")
	MigrateMessagesListener longFailureMigrationConsumer() {
		return new MigrateMessagesListener(getFailureQueueName(), getQueueName());
	}

	protected String getQueueName() {
		return "long";
	}

	protected String getFailureQueueName() {
		return getQueueName() + "-failure";
	}
}
