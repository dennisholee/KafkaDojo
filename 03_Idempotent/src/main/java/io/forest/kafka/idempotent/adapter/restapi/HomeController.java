package io.forest.kafka.idempotent.adapter.restapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.forest.kafka.idempotent.port.PublishMessage;
import io.forest.kafka.idempotent.port.command.PublishMessageCommand;

@RestController("/")
public class HomeController {

	@Autowired
	PublishMessage publishMessage;

	@GetMapping(path = "/publish")
	public ResponseEntity<String> publish(@RequestParam("count") int count) {

		PublishMessageCommand publishMessageCommand = new PublishMessageCommand().setCount(count > 0 ? count : 1);
		this.publishMessage.handle(publishMessageCommand);
		return ResponseEntity.ok("OK");
	}
}
