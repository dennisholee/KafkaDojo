package io.forest.kafka.idempotent.application;

import java.util.stream.IntStream;

import io.forest.kafka.idempotent.port.BetaGateway;
import io.forest.kafka.idempotent.port.PublishMessage;
import io.forest.kafka.idempotent.port.command.PublishMessageCommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PublishMessageApplication implements PublishMessage {

	@NonNull
	BetaGateway alphaGateway;

	@Override
	public void handle(PublishMessageCommand command) {
		IntStream.range(0, command.getCount())
				.forEach(it -> alphaGateway.doFoo());
	}
}
