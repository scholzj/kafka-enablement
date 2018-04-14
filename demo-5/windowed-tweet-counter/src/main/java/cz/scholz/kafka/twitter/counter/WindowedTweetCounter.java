package cz.scholz.kafka.twitter.counter;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.kstream.internals.WindowedDeserializer;
import org.apache.kafka.streams.kstream.internals.WindowedSerializer;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.WindowStore;

import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class WindowedTweetCounter {
    public static void main(final String[] args) throws Exception {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "twitter-windowed-counter");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> tweets = builder.stream("twitter-feed");

        KTable<Windowed<String>, Long> counts = tweets.groupBy((key, value) -> "")
                .windowedBy(TimeWindows.of(TimeUnit.SECONDS.toMillis(60)))
                .count(Materialized.<String, Long, WindowStore<Bytes, byte[]>>as("twitter-windowed-counter-store").withCachingEnabled());

        WindowedSerializer<String> windowedSerializer = new WindowedSerializer<>(Serdes.String().serializer());
        WindowedDeserializer<String> windowedDeserializer = new WindowedDeserializer<>(Serdes.String().deserializer(), TimeUnit.SECONDS.toMillis(60));
        Serde<Windowed<String>> windowedSerde = Serdes.serdeFrom(windowedSerializer, windowedDeserializer);

        counts.toStream()
                .to("twitter-windowed-counter", Produced.with(windowedSerde, Serdes.Long()));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
    }
}
