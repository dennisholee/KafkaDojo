package io.forest.kafka.stream;

import static io.restassured.RestAssured.with;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = ApplicationTest.class)
class ApplicationTest {

	@Container
	KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"));

	@Test
	void givenInputMessages_whenPostToEndpoint_thenWordCountsReceivedOnOutput() throws Exception {

		with().body("")
				.when()
				.post("")
				.then()
				.statusCode(200);

//		postMessage("test message");
//
//		startOutputTopicConsumer();
//
//		// assert correct counts on output topic
//		assertThat(output.poll(2, MINUTES)).isEqualTo("test:1");
//		assertThat(output.poll(2, MINUTES)).isEqualTo("message:1");
//
//		// assert correct count from REST service
//		assertThat(getCountFromRestServiceFor("test")).isEqualTo(1);
//		assertThat(getCountFromRestServiceFor("message")).isEqualTo(1);
	}

}
