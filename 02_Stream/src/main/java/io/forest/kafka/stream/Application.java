package io.forest.kafka.stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import io.forest.kafka.stream.conf.KafkaConfig;

@SpringBootApplication
@Import({ KafkaConfig.class })
public class Application {

	public static void main(String args[]) {
		SpringApplication.run(Application.class, args);
	}
}
