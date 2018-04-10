package cz.scholz.kafka.twitter.transformer;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Locale;
import java.util.Properties;

public class Transformer {
    public static void main(final String[] args) throws Exception {
        final Serializer<JsonNode> jsonSerializer = new JsonSerializer();
        final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
        final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "transformer");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "172.30.170.63:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();
        builder.stream("twitter-feed", Consumed.with(Serdes.String(), jsonSerde))
                .map((key, value) -> KeyValue.pair(value.get("user").get("screen_name").asText(), value.get("text").asText()))
                .mapValues(value -> value.replaceAll(System.getProperty("line.separator"), ""))
                .to("tweets");

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
    }
}
