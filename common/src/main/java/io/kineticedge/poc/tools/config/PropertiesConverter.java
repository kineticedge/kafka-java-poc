package io.kineticedge.poc.tools.config;

import com.beust.jcommander.converters.BaseConverter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;


public class PropertiesConverter extends BaseConverter<Map<String, Object>> {
    public PropertiesConverter(String optionName) {
        super(optionName);
    }

    public Map<String, Object> convert(String value) {
        try {
            final Properties properties = new Properties();
            properties.load(new FileInputStream(value));
            return convert(properties);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static HashMap<String, Object> convert(final Properties prop) {
        return prop.entrySet().stream().collect(
                Collectors.toMap(
                        entry -> (String) entry.getKey(),
                        Map.Entry::getValue,
                        (prev, next) -> next,
                        HashMap::new
                ));
    }
}

