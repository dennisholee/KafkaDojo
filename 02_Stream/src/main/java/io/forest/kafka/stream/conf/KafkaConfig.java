package io.forest.kafka.stream.conf;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.streams.StreamsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@EnableKafka
@EnableKafkaStreams
public class KafkaConfig {

	@Value(value = "${spring.kafka.bootstrap-servers}")
	String bootstrapAddress;

	@Bean
	@Scope("prototype")
	StreamsBuilder streamsBuilder() {
		return new StreamsBuilder();
	}

	@Bean
	StreamsBuilderFactoryBean factoryBean() {
		return new StreamsBuilderFactoryBean();
	}

	@Bean
	ProducerFactory<String, String> producerFactory() {
		return new DefaultKafkaProducerFactory<>(null);
	}

	@Bean
	KafkaTemplate kafkaProducer(@Autowired ProducerFactory producerFactory) {
//		return null;
		return new KafkaTemplate<>(producerFactory);
	}
}
