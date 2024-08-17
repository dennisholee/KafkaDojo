package io.forest.kafka.stream.adapter.restapi;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WordCountController {

	StreamsBuilderFactoryBean factoryBean;

	KafkaProducer kafkaProducer;

	public WordCountController(StreamsBuilderFactoryBean factoryBean, KafkaProducer kafkaProducer) {
		this.factoryBean = factoryBean;
		this.kafkaProducer = kafkaProducer;
	}

	@GetMapping
	public Long getWordCount(@PathVariable String word) {
		KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
		ReadOnlyKeyValueStore<String, Long> counts = kafkaStreams
				.store(StoreQueryParameters.fromNameAndType("counts", QueryableStoreTypes.keyValueStore()));
		return counts.get(word);
	}

	@PostMapping("/message")
	public void addMessage(@RequestBody String message) {
		ProducerRecord record = new ProducerRecord<String, String>(message, message);
		kafkaProducer.send(record);
	}
}
