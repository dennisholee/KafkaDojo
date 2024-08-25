package io.forest.kafka.idempotent.port;

import io.forest.kafka.idempotent.port.command.PublishMessageCommand;

public interface PublishMessage {
	
	void handle(PublishMessageCommand command);

}
