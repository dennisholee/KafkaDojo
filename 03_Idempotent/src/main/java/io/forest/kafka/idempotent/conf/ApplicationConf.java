package io.forest.kafka.idempotent.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import io.forest.kafka.idempotent.application.PublishMessageApplication;
import io.forest.kafka.idempotent.port.BetaGateway;
import io.forest.kafka.idempotent.port.PublishMessage;

public class ApplicationConf {

	@Bean
	PublishMessage publishMessage(@Autowired BetaGateway alphaGateway) {
		return new PublishMessageApplication(alphaGateway);
	}
}
