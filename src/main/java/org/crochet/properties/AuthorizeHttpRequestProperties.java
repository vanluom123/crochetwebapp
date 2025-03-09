package org.crochet.properties;

import lombok.Data;

@Data
public class AuthorizeHttpRequestProperties {
    private String[] allowedOrigins;
}
