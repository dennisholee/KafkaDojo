package io.forest.kafka.stream.adapter.kafka;

import java.util.Arrays;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.ValueMapper;

public class WordCountProcessor {

	private static final Serde<String> STRING_SERDE = Serdes.String();

	StreamsBuilder streamsbuilder;

	public WordCountProcessor(StreamsBuilder streamsbuilder) {
		this.streamsbuilder = streamsbuilder;
	}

	Topology buildPipline(String inputTopic, String outputTopic) {

		KTable<String, Long> count = streamsbuilder.stream(inputTopic, Consumed.with(STRING_SERDE, STRING_SERDE))
				.mapValues((ValueMapper<String, String>) String::toLowerCase)
				.flatMapValues(value -> Arrays.asList(value.split("\\W+")))
				.groupBy((key, word) -> word, Grouped.with(STRING_SERDE, STRING_SERDE))
				.count();

		count.toStream()
				.to(outputTopic);

		return streamsbuilder.build();
	}
}
