package org.crochet.security.oauth2.user;

import java.util.Map;

public abstract class AbstractOAuth2User {
    protected Map<String, Object> attributes;

    public AbstractOAuth2User(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public abstract String getId();

    public abstract String getName();

    public abstract String getEmail();

    public abstract String getImageUrl();
}
