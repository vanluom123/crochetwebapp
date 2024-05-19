package org.crochet.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private final Auth auth = new Auth();
    private final OAuth2 oauth2 = new OAuth2();
    private final Cors cors = new Cors();

    @Setter
    @Getter
    public static class Auth {
        private String tokenSecret;
        private long tokenExpirationMs;
        private long refreshTokenExpirationMs;
    }

    @Getter
    @Setter
    public static final class OAuth2 {
        private List<String> authorizedRedirectUris;
    }

    @Getter
    @Setter
    public static final class Cors {
        private List<String> allowedOrigins;
    }
}
