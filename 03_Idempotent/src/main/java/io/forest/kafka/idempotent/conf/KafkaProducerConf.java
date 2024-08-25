package io.forest.kafka.idempotent.conf;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.MicrometerProducerListener;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.micrometer.KafkaRecordSenderContext;
import org.springframework.kafka.support.micrometer.KafkaTemplateObservationConvention;

import io.micrometer.common.KeyValues;
import io.micrometer.core.instrument.MeterRegistry;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@EnableConfigurationProperties
public class KafkaProducerConf {

	@Bean
	public ProducerFactory<Integer, String> producerFactory(@Autowired KafkaProperties kafkaProperties
	                                                        , @Autowired MeterRegistry meterRegistry
	                                                        ) {

		Map<String, Object> configProps = new HashMap<>(kafkaProperties.buildProducerProperties());
		configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // Enable idempotence

		ProducerFactory<Integer, String> factory = new DefaultKafkaProducerFactory<>(configProps);
		 factory.addListener(new MicrometerProducerListener<>(meterRegistry)); // expose metrics to actuator

		return factory;
	}


	@Bean
	public KafkaTemplate<Integer, String> kafkaTemplate(@Autowired ProducerFactory<Integer, String> producerFactory) {
		KafkaTemplate<Integer, String> kafkaTemplate = new KafkaTemplate<Integer, String>(producerFactory);
		kafkaTemplate.setObservationEnabled(true);

		kafkaTemplate.setObservationConvention(new KafkaTemplateObservationConvention() {
			@Override
			public KeyValues getLowCardinalityKeyValues(KafkaRecordSenderContext context) {
				return KeyValues.of("topic",
						context.getDestination(),
						"id",
						String.valueOf(context.getRecord()
								.key()));
			}
		});
		return kafkaTemplate;
	}
	
	@Bean
	OtlpHttpSpanExporter otlpHttpSpanExporter() {
	    return OtlpHttpSpanExporter.builder()
	            .setEndpoint("http://localhost:4318/v1/traces")
	            .build();
	}
}
