package io.forest.kafka.idempotent;

import java.util.concurrent.ExecutionException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;

import io.forest.kafka.idempotent.conf.AdapterConf;
import io.forest.kafka.idempotent.conf.ApplicationConf;
import io.forest.kafka.idempotent.conf.KafkaAdminConf;
import io.forest.kafka.idempotent.conf.KafkaProducerConf;
import lombok.extern.log4j.Log4j2;

@SpringBootApplication
@EnableKafka
@Import({ AdapterConf.class, ApplicationConf.class, KafkaAdminConf.class, KafkaProducerConf.class })
@Log4j2
public class Application {

	public static void main(String[] args)	 {
		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
		/*
		 * log.info("Proceeding to fetch AlphaGateway bean.");
		 * 
		 * AlphaGateway alphaGateway = context.getBean(AlphaGateway.class);
		 * 
		 * log.info("Proceeding to invoke alphaGateway.doFoo");
		 * 
		 * long startTime = System.nanoTime();
		 * 
		 * int count = 100;
		 * 
		 * // ExecutorService executorService = Executors.newFixedThreadPool(10); //
		 * IntStream.range(0, count) // .forEach(it -> { // Future<String> future =
		 * executorService.submit(() -> alphaGateway.doFoo()); // }); IntStream.range(0,
		 * count) .forEach(it -> alphaGateway.doFoo());
		 * 
		 * long endTime = System.nanoTime();
		 * 
		 * long executionTime = (endTime - startTime) / 1000000;
		 * 
		 * System.out.println("Sending " + count + " messages takes " + executionTime +
		 * "ms");
		 */
	}

}
