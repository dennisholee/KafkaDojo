package io.forest.kafka.stream.conf;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.streams.StreamsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;

import io.forest.kafka.stream.adapter.kafka.WordCountProcessor;
import io.forest.kafka.stream.adapter.restapi.WordCountController;

public class AdapterConfig {

	@Bean
	WordCountController wordCountController(@Autowired StreamsBuilderFactoryBean factoryBean,
			@Autowired KafkaProducer kafkaProducer) {
		return new WordCountController(factoryBean, kafkaProducer);
	}

	@Bean
	WordCountProcessor wordCountProcessor(@Autowired StreamsBuilder streamsBuilder) {
		return new WordCountProcessor(streamsBuilder);
	}
}
