package org.crochet.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "authorize.http-request")
public class AuthorizeHttpRequestProperties {
    private String[] permitAll;
    private String[] authenticated;
}
