package io.kineticedge.poc.consumer;

import com.beust.jcommander.Parameter;
import io.kineticedge.poc.tools.config.BaseOptions;
import io.kineticedge.poc.tools.serde.JsonDeserializer;
import io.kineticedge.poc.tools.serde.JsonSerializer;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Options extends BaseOptions {

    @Parameter(names = { "--from-topic" }, description = "")
    private String fromTopic = "orders-purchase";

    @Parameter(names = { "--to-topic" }, description = "")
    private String toTopic = "orders-purchase-replicated";

    @Parameter(names = { "--poll-duration" }, description = "poll duration")
    private Duration pollDuration = Duration.ofMillis(500L);

    /**
     * Kafka Properties for this application that are defaults, but you allow deployments to change them.
     */
    private Map<String, Object> producerDefault() {
        return Map.ofEntries(
                Map.entry(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "PLAINTEXT"),
                Map.entry(ProducerConfig.ACKS_CONFIG, "all"),
                Map.entry(ProducerConfig.LINGER_MS_CONFIG, 50)
        );
    }

    private Map<String, Object> consumerDefault() {
        return Map.ofEntries(
                Map.entry(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "PLAINTEXT"),
                Map.entry(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
        );
    }

    /**
     * Kafka Properties for this application that cannot be changed.
     *
     * This should be a small list, but serializers are typically what falls into place here.
     */
    private Map<String, Object> producerImmutables() {
        return Map.ofEntries(
                Map.entry(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()),
                Map.entry(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName())
        );
    }

    private Map<String, Object> consumerImmutables() {
        return Map.ofEntries(
                Map.entry(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()),
                Map.entry(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName())
        );
    }

    @SuppressWarnings("unused")
    public Map<String, Object> getKafkaProducerConfigurations() {
        Map<String, Object> properties = new HashMap<>();

        properties.putAll(producerDefault());
        properties.putAll(getProducerConfig());
        properties.putAll(producerImmutables());

        return properties;
    }

    @SuppressWarnings("unused")
    public Map<String, Object> getKafkaConsumerConfigurations() {
        Map<String, Object> properties = new HashMap<>();

        properties.putAll(consumerDefault());
        properties.putAll(getConsumerConfig());
        properties.putAll(consumerImmutables());

        return properties;
    }

}
