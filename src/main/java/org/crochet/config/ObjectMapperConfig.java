package org.crochet.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {
    @Bean
    ObjectMapper objectMapper() {
        JsonMapper.Builder builder = JsonMapper.builder();
        
        // Add modules
        builder.addModule(new JavaTimeModule());
        
        // Configure deserialization features
        builder.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
               .configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false)
               .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
               
        // Configure serialization features       
        builder.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
               .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        
        // Configure type handling and visibility
        builder.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL)
               .visibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        
        return builder.build();
    }
}
