package home.rabbitmqtest.id.migration;

import home.rabbitmqtest.config.AbstractMigrationMessage;

public class LongMigrationMessage extends AbstractMigrationMessage<Long> {

	public LongMigrationMessage(Long payload) {
		super(payload);
	}

	public LongMigrationMessage() {
		super();
	}
}
