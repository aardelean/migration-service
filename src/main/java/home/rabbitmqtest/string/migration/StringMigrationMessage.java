package home.rabbitmqtest.string.migration;

import home.rabbitmqtest.config.AbstractMigrationMessage;

public class StringMigrationMessage extends AbstractMigrationMessage<String> {

	public StringMigrationMessage(String payload) {
		super(payload);
	}

	public StringMigrationMessage() {
		super();
	}
}
