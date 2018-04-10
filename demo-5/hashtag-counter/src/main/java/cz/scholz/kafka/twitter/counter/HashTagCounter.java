package cz.scholz.kafka.twitter.counter;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Locale;
import java.util.Properties;

public class HashTagCounter {
    public static void main(final String[] args) throws Exception {
        final Serializer<JsonNode> jsonSerializer = new JsonSerializer();
        final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
        final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "hashtag-counter");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "172.30.170.63:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> hashTags = builder
                .stream("twitter-feed", Consumed.with(Serdes.String(), jsonSerde))
                .flatMapValues(HashTagCounter::getHashTags)
                .mapValues(value -> value.toLowerCase(Locale.getDefault()));
        KTable<String, Long> counts = hashTags.groupBy((key, value) -> value).count();

        counts.toStream()
                .to("twitter-hashtag-counter", Produced.with(Serdes.String(), Serdes.Long()));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
    }

    private static Iterable<? extends String> getHashTags(JsonNode value) {
        return value.get("entities").get("hashtags").findValuesAsText("text");
    }
}
