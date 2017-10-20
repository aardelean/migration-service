package home.rabbitmqtest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import home.rabbitmqtest.config.QueueConfiguration;
import home.rabbitmqtest.id.migration.LongQueueMigrationConfiguration;
import home.rabbitmqtest.string.migration.StringMigrationQueueConfiguration;

@SpringBootApplication
@Import({ QueueConfiguration.class, LongQueueMigrationConfiguration.class, StringMigrationQueueConfiguration.class})
public class RabbitMqTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(RabbitMqTestApplication.class, args);
	}
}
