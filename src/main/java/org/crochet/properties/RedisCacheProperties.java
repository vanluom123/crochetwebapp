package org.crochet.properties;

import lombok.Data;

@Data
public class RedisCacheProperties {
    private String host;
    private int port;
    private String username;
    private String password;
    private boolean isSsl;

    public boolean isSsl() {
        return username != null && password != null && isSsl;
    }
}
