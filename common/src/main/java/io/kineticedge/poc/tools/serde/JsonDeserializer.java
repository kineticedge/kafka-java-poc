package io.kineticedge.poc.tools.serde;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.TimeZone;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class JsonDeserializer<T> implements Deserializer<T> {

    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .setTimeZone(TimeZone.getDefault())
                    .registerModule(new JavaTimeModule());

    @SuppressWarnings("unused")
    public JsonDeserializer() {
    }

    @Override
    public void configure(Map<String, ?> props, boolean isKey) {
    }

    @Override
    public T deserialize(String topic, byte[] bytes) {
        if (bytes == null)
            return null;

        try {
            JsonNode node = OBJECT_MAPPER.readTree(bytes);

            //
            // This JsonDeserializer is using JsonTypeInfo to know the actual class to use to deserialize.
            // In most production use cases, this is not the pattern most follow -- but for demonstrations
            // it is super useful as it makes JSON process mimic Avro and requires creating an instance for
            // every type.
            //
            if (node.get("$type") == null || !node.get("$type").isTextual()) {
                throw new SerializationException("missing '$type' field.");
            }

            return read(node.get("$type").asText(), node);
        } catch (final IOException e) {
            throw new SerializationException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private T read(final String className, JsonNode jsonNode) {
        try {
            return  (T) OBJECT_MAPPER.convertValue(jsonNode, Class.forName(className));
        } catch (final ClassNotFoundException e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public void close() {
    }
}