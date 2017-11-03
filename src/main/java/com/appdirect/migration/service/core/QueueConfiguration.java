package com.appdirect.migration.service.core;

import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfiguration {
	private static final String QUEUE_FAILURE_SUFFIX = "-failure";

	@Autowired
	private ConnectionFactory connectionFactory;
	@Autowired
	private MessageConverter messageConverter;
	@Autowired
	private List<AbstractQueueMessageManager> queueMessageManagers;
	@Autowired
	private ApplicationContext context;
	@Autowired
	private Exchange exchange;
	@Autowired
	private MigrateFailedMessagesListener migrateFailedMessagesListener;

	@Bean
	TopicExchange exchange() {
		return new TopicExchange("migration-exchange");
	}

	@Bean
	public MessageConverter jsonMessageConverter(){
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public MigrateFailedMessagesListener migrateMessagesListener() {
		return new MigrateFailedMessagesListener(QUEUE_FAILURE_SUFFIX);
	}

	@Bean
	public MessageConsumer migrationMessageManager() {
		return new MessageConsumer(QUEUE_FAILURE_SUFFIX);
	}

	@PostConstruct
	public void createRabbitMQBindings() {
		queues();
		failureQueues();
		failureMigrationListeners();
	}

	private void failureMigrationListeners() {
		queueMessageManagers.stream()
				.map(p -> p.queueName() + QUEUE_FAILURE_SUFFIX)
				.forEach(queueName -> migrationFailedMessagesListener(migrateFailedMessagesListener, queueName));
//				.forEach(consumer -> getQueueMessageManager(consumer.getQueueNames()[0]).setFailureMessagesConsumer(consumer));
	}

	private AbstractQueueMessageManager getQueueMessageManager(String failureQueue) {
		return queueMessageManagers.stream()
				.filter(p -> failureQueue.equals(p.queueName() + QUEUE_FAILURE_SUFFIX))
				.findFirst().orElseThrow(() -> new IllegalArgumentException("Could not find any processor for failureQueue " + failureQueue));
	}

	private void queues() {
		queueMessageManagers.stream()
				.map(QueueMessageManager::queueName)
				.map(q -> createDynamicQueues(q))
				.forEach(queueName -> createDynamicBinding(queueName, exchange.getName()));
	}

	private void failureQueues() {
		queueMessageManagers.stream()
				.map(p -> p.queueName() + QUEUE_FAILURE_SUFFIX)
				.map(q -> createDynamicQueues(q))
				.forEach(queueName -> createDynamicBinding(queueName, exchange.getName()));
	}

	public String createDynamicQueues(String queueName) {
		BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry)context.getAutowireCapableBeanFactory();
		final BeanDefinition queue = BeanDefinitionBuilder
				.rootBeanDefinition(Queue.class)
				.setScope(BeanDefinition.SCOPE_PROTOTYPE)
				.addConstructorArgValue(queueName)
				.getBeanDefinition();
		beanDefinitionRegistry.registerBeanDefinition(queueName, queue);
		return queueName;
	}

	public void createDynamicBinding(String queueName, String exchangeName) {
		BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry)context.getAutowireCapableBeanFactory();
		final BeanDefinition binding = BeanDefinitionBuilder
				.rootBeanDefinition(Binding.class)
				.setScope(BeanDefinition.SCOPE_PROTOTYPE)
				.addConstructorArgValue(queueName)
				.addConstructorArgValue(Binding.DestinationType.QUEUE)
				.addConstructorArgValue(exchangeName)
				.addConstructorArgValue(queueName)
				.addConstructorArgValue(new HashMap<>())
				.getBeanDefinition();
		beanDefinitionRegistry.registerBeanDefinition("binding-" + queueName, binding);
	}

	private SimpleMessageListenerContainer migrationFailedMessagesListener(MigrateFailedMessagesListener messageListener, String ...queueNames) {
		BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry)context.getAutowireCapableBeanFactory();
		final BeanDefinition containerListener = BeanDefinitionBuilder
				.rootBeanDefinition(SimpleMessageListenerContainer.class)
				.addPropertyValue("connectionFactory", connectionFactory)
				.addPropertyValue("queueNames", queueNames)
				.addPropertyValue("messageConverter", messageConverter)
				.addPropertyValue("messageListener", messageListener)
				.addPropertyValue("autoStartup", false)
				.getBeanDefinition();
		beanDefinitionRegistry.registerBeanDefinition("migrateListener-" + queueNames[0], containerListener);
		return (SimpleMessageListenerContainer)containerListener.getSource();
	}

	@Bean(name = "messageListenerContainer")
	public SimpleMessageListenerContainer messageListenerContainer(List<QueueMessageManager> processors, MessageConsumer messageConsumer) {
		String[] queueNames = processors.stream().map(QueueMessageManager::queueName).toArray(String[]::new);
		return buildSimpleMessageListenerContainer(queueNames, messageConsumer, true);
	}

	private SimpleMessageListenerContainer buildSimpleMessageListenerContainer(String[] queueNames, MessageListener messageListener, boolean autoStart) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(queueNames);
		container.setMessageConverter(messageConverter);
		container.setMessageListener(messageListener);
		container.setAutoStartup(autoStart);
		container.setAcknowledgeMode(AcknowledgeMode.AUTO);
		return container;
	}
}
