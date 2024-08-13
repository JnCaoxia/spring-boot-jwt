package murraco.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaMessageConsumer {

    private static final String TOPIC = "maxwell";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String GROUP_ID = "kafkaGroup";

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singletonList(TOPIC));

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("Consumed record with key %s and value %s%n", record.key(), record.value());

                    try {
                        JsonNode jsonNode = objectMapper.readTree(record.value());
                        // 解析JSON
                        String database = jsonNode.get("database").asText();
                        String table = jsonNode.get("table").asText();
                        String type = jsonNode.get("type").asText();
                        JsonNode data = jsonNode.get("data");

                        int id = data.get("id").asInt();
                        String name = data.get("name").asText();

                        System.out.printf("Parsed message: database=%s, table=%s, type=%s, data=%s%n",
                                database, table, type, data);
                    } catch (Exception e) {
                        System.err.printf("Failed to parse JSON message: %s%n", record.value());
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            consumer.close();
        }
    }
}
