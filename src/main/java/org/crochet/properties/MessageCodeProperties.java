package org.crochet.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "message")
public class MessageCodeProperties {
    private Map<String, Integer> codes;
    public int getCode(String key) {
        return codes.get(key);
    }
}
