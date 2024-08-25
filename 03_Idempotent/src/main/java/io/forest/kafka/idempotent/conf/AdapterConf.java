package io.forest.kafka.idempotent.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

import io.forest.kafka.idempotent.adapter.beta.BetaKafkaGateway;
import io.forest.kafka.idempotent.port.BetaGateway;

public class AdapterConf {

	@Bean
	BetaGateway betaGateway(@Autowired KafkaTemplate<Integer, String> kafkaTemplate) {
		return new BetaKafkaGateway(kafkaTemplate);
	}
}
