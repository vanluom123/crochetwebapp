package org.crochet.properties;

import lombok.Data;

@Data
public class RedisCacheProperties {
    private String host;
    private int port;
    private String username;
    private String password;
}
