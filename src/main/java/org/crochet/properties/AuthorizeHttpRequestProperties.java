package org.crochet.properties;

import lombok.Data;

@Data
public class AuthorizeHttpRequestProperties {
    private String[] authenticated;
    private String[] allowedOrigins;
}
