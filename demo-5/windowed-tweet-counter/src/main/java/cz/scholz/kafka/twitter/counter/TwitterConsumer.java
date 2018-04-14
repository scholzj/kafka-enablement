package cz.scholz.kafka.twitter.counter;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.kstream.internals.WindowedDeserializer;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class TwitterConsumer {
    public static void main(final String[] args) throws Exception {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "TwitterConsumer");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
        Deserializer<Windowed<String>> windowedDeserializer = new WindowedDeserializer<String>(Serdes.String().deserializer(), TimeUnit.SECONDS.toMillis(60));
        Deserializer<Long> longDeserializer = new LongDeserializer();

        KafkaConsumer<Windowed<String>, Long> consumer = new KafkaConsumer<Windowed<String>, Long>(props, windowedDeserializer, longDeserializer);
        consumer.subscribe(Collections.singletonList("twitter-windowed-counter"));

        while (true)
        {
            ConsumerRecords<Windowed<String>, Long> records = consumer.poll(1000);

            for (ConsumerRecord<Windowed<String>, Long> record : records)
            {
                System.out.println("-I- received message: " + sdf.format(new Date(record.key().window().start())) + " - " + sdf.format(new Date(record.key().window().end())) + " / " + record.value());
            }
        }
    }
}
