package org.crochet.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * UserPrincipal class
 */
public class UserPrincipal implements OAuth2User, UserDetails {
    @Getter
    private final UUID id;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes;

    /**
     * Constructor of UserPrincipal class
     *
     * @param id          id
     * @param email       email
     * @param password    password
     * @param authorities authorities
     */
    public UserPrincipal(UUID id,
                         String email,
                         String password,
                         Collection<? extends GrantedAuthority> authorities,
                         Map<String, Object> attributes) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.attributes = attributes;
    }

    /**
     * Returns a new instance of {@link UserPrincipalBuilder} to build a {@link UserPrincipal}.
     *
     * @return A new instance of {@link UserPrincipalBuilder}.
     */
    public static UserPrincipalBuilder builder() {
        return new UserPrincipalBuilder();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }
}
