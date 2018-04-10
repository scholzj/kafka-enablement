package cz.scholz.kafka.twitter.counter;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.kstream.internals.WindowedDeserializer;
import org.apache.kafka.streams.kstream.internals.WindowedSerializer;
import org.apache.kafka.streams.state.WindowStore;

import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class WindowedHashTagCounter {
    public static void main(final String[] args) throws Exception {
        final Serializer<JsonNode> jsonSerializer = new JsonSerializer();
        final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
        final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "windowed-hashtag-counter");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "172.30.170.63:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, JsonNode> tweets = builder.stream("twitter-feed", Consumed.with(Serdes.String(), jsonSerde));

        KStream<String, String> hashTags = tweets.flatMapValues(WindowedHashTagCounter::getHashTags).mapValues(value -> value.toLowerCase(Locale.getDefault()));
        KTable<Windowed<String>, Long> counts = hashTags.groupBy((key, value) -> value)
                .windowedBy(TimeWindows.of(TimeUnit.SECONDS.toMillis(60)))
                .count(Materialized.<String, Long, WindowStore<Bytes, byte[]>>as("twitter-windowed-hashtag-counter-store"));

        WindowedSerializer<String> windowedSerializer = new WindowedSerializer<>(Serdes.String().serializer());
        WindowedDeserializer<String> windowedDeserializer = new WindowedDeserializer<>(Serdes.String().deserializer(), 60);
        Serde<Windowed<String>> windowedSerde = Serdes.serdeFrom(windowedSerializer, windowedDeserializer);

        counts.toStream()
                .to("twitter-windowed-hashtag-counter", Produced.with(windowedSerde, Serdes.Long()));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
    }

    private static Iterable<? extends String> getHashTags(JsonNode value) {
        return value.get("entities").get("hashtags").findValuesAsText("text");
    }
}
