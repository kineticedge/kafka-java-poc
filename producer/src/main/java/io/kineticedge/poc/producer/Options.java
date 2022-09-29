package io.kineticedge.poc.producer;

import com.beust.jcommander.Parameter;
import io.kineticedge.poc.tools.config.BaseOptions;
import io.kineticedge.poc.tools.serde.JsonSerializer;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * There are many ways to capture command line switches and place them into the application; this uses JCommander.
 *
 * If you are using a framework, like SpringBoot, their mechanisms would (and should) be used.  However, it is good
 * practice to see the low-level properties used for Apache Kafka w/out additional frameworks. Learn each part
 * and understand what each is doing -- makes it easier to migrate to other frameworks or libraries.
 *
 */
@Getter
@Setter
public class Options extends BaseOptions {

    @Parameter(names = { "--line-items" }, description = "use x:y for a range, single value for absolute")
    private String lineItemCount= "1";

    @Parameter(names = { "--pause" }, description = "")
    private Long pause = 1000L;

    @Parameter(names = { "--skus" }, description = "")
    private List<Integer> skus;

    @Parameter(names = { "--max-sku" }, description = "")
    private int maxSku = getNumberOfProducts();

    /**
     * Kafka Properties for this application that are defaults, but you allow deployments to change them.
     *
     * This is a great way to make SASL_PLAIN or security protocol, PLAIN your SASL Mechanism, and all those
     * properties that need to be set the default so most properties file can omit them, but by allowing them
     * to be overridden still allows developers to run application locally using PLAINTEXT -- but the
     * responsibility is on them to add the properties -- make production the least chatty configuration.
     */
    private Map<String, Object> defaults() {
        return Map.ofEntries(
                Map.entry(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "PLAINTEXT"),
                Map.entry(ProducerConfig.ACKS_CONFIG, "all"),
                Map.entry(ProducerConfig.LINGER_MS_CONFIG, 200)
        );
    }

    /**
     * Kafka Properties for this application that cannot be changed.
     *
     * If the application cannot tolerate a kafka property from being changed, it should be added here.
     * Frameworks (such as Spring-Kafka) may not make it easy to supply this pattern, as Spring Configuration
     * will explose all properties to the configuration file.
     *
     * This should be a small list, but serializers are typically what falls into place here.
     */
    private Map<String, Object> immutables() {
        return Map.ofEntries(
                Map.entry(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()),
                Map.entry(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName())
        );
    }

    public Map<String, Object> getKafkaProperties() {
        Map<String, Object> properties = new HashMap<>();

        properties.putAll(defaults());
        properties.putAll(getProducerConfig());
        properties.putAll(immutables());

        return properties;
    }

}
