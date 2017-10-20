package home.rabbitmqtest.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfiguration {

	@Autowired
	private ConnectionFactory connectionFactory;

	@Autowired
	private MessageConverter messageConverter;

	@Bean
	TopicExchange exchange() {
		return new TopicExchange("test-exchange");
	}

	@Bean
	public MessageConverter jsonMessageConverter(){
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public ContainerBuilder containerBuilder() {
		return new ContainerBuilder();
	}

	public class ContainerBuilder {
		public SimpleMessageListenerContainer buildSimpleMessageListenerContainer(MigrationMessageListener consumer) {
			SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
			container.setConnectionFactory(connectionFactory);
			container.setQueueNames(consumer.getListeningQueueName());
			container.setMessageConverter(messageConverter);
			container.setMessageListener(consumer);
			container.setAcknowledgeMode(AcknowledgeMode.AUTO);
			return container;
		}

		public SimpleMessageListenerContainer buildFailureMigrationListenerContainer(MigrationMessageListener consumer) {
			SimpleMessageListenerContainer container = buildSimpleMessageListenerContainer(consumer);
			container.setAutoStartup(false);
			return container;
		}
	}
}
