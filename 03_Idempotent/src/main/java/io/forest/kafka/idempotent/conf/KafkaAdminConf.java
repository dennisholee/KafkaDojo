package io.forest.kafka.idempotent.conf;

import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class KafkaAdminConf {


	@Value("${application.adapter.alpha.kafka.topic}")
	String topicAlphaName;

//	@Bean
//	KafkaAdmin kafkaAdmin() {
//		String kafkaConnection = "localhost:29092";
//
//		log.info("Kafka connection details [conn={}]", kafkaConnection);
//		Map<String, Object> configs = Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConnection);
//
//		KafkaAdmin kafkaAdmin = new KafkaAdmin(configs);
//		// kafkaAdmin.setFatalIfBrokerNotAvailable(true);
//		return kafkaAdmin;
//	}

//	@Bean
//	NewTopic topicFoo() {
//		log.info("Create kafka topic [name={}]", topicAlphaName);
//		return TopicBuilder.name(topicAlphaName)
////				.partitions(3)
////				.replicas(1)
////				.compact()
//				.build();
//	}
}
