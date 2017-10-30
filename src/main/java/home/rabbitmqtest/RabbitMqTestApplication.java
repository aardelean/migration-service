package home.rabbitmqtest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import home.rabbitmqtest.config.QueueConfiguration;

@SpringBootApplication
@Import({ QueueConfiguration.class})
public class RabbitMqTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(RabbitMqTestApplication.class, args);
	}
}
