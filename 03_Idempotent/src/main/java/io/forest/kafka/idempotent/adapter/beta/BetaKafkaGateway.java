package io.forest.kafka.idempotent.adapter.beta;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.SendResult;

import io.forest.kafka.idempotent.port.BetaGateway;
import io.micrometer.core.annotation.Timed;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
public class BetaKafkaGateway implements BetaGateway {

	@Value("${application.adapter.alpha.kafka.topic}")
	String topicAlphaName;

	@NonNull
	KafkaTemplate<Integer, String> kafkaTemplate;

	@Override
	public void doFoo() {
		int partition = 0;
		ProducerRecord<Integer, String> producerRecord = new ProducerRecord<Integer, String>(
				topicAlphaName,
				partition,
				1,
				"Hello World");

		CompletableFuture<SendResult<Integer, String>> completableFuture = this.kafkaTemplate.send(producerRecord);
		completableFuture.whenComplete((result,
										ex) -> {
			log.info("Offset={}, ex={}",
					result.getRecordMetadata()
							.offset(),
					ex);

		});
	}
}
