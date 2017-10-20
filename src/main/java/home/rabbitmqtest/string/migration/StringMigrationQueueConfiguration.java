package home.rabbitmqtest.string.migration;

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
import home.rabbitmqtest.id.migration.LongMigrationConsumer;

@Configuration
public class StringMigrationQueueConfiguration {

	@Autowired
	private QueueConfiguration.ContainerBuilder containerBuilder;

	@Bean(name = "stringQueue")
	Queue queue() {
		return new Queue(getQueueName(), true);
	}

	@Bean(name = "stringQueueFailure")
	Queue failedQueue() {
		return new Queue(getFailureQueueName(), true);
	}

	@Bean
	Binding bindingString(@Qualifier("stringQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(getQueueName());
	}

	@Bean
	Binding bindingStringFailureQueue(@Qualifier("stringQueueFailure") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(getFailureQueueName());
	}

	@Bean
	public StringMigrationConsumer stringMigrationConsumer(@Qualifier("stringRetryFailureContainer") SimpleMessageListenerContainer failureMigrationContainer)  {
		return new StringMigrationConsumer(getQueueName(), getFailureQueueName(), failureMigrationContainer);
	}

	@Bean
	SimpleMessageListenerContainer containerStringMigration(LongMigrationConsumer consumer) {
		return containerBuilder.buildSimpleMessageListenerContainer(consumer);
	}

	@Bean(name = "stringRetryFailureContainer")
	SimpleMessageListenerContainer containerStringRetryFailureMigration(@Qualifier("stringFailureMigrationListener") MigrateMessagesListener migrateMessagesListener) {
		return containerBuilder.buildFailureMigrationListenerContainer(migrateMessagesListener);
	}

	@Bean(name = "stringFailureMigrationListener")
	MigrateMessagesListener stringFailureMigrationConsumer() {
		return new MigrateMessagesListener(getFailureQueueName(), getQueueName());
	}

	protected String getQueueName() {
		return "string";
	}

	protected String getFailureQueueName() {
		return getQueueName() + "-failure";
	}
}
