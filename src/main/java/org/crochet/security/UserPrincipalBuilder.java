package org.crochet.security;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.crochet.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * UserPrincipalBuilder class
 */
@Setter
@Accessors(fluent = true)
public class UserPrincipalBuilder {
    private UUID id;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    /**
     * Builds and returns a new {@link UserPrincipal} instance using the provided details and authorities.
     *
     * @return A new {@link UserPrincipal} instance.
     */
    public UserPrincipal build() {
        return new UserPrincipal(id, email, password, authorities, attributes);
    }

    /**
     * Creates a {@link UserPrincipalBuilder} for the provided {@link User}, setting its details and authorities.
     *
     * @param user The {@link User} for which to create the {@link UserPrincipalBuilder}.
     * @return A {@link UserPrincipalBuilder} with the details and authorities of the provided user.
     */
    public UserPrincipalBuilder createUser(User user) {
        // Create a list of GrantedAuthority with a single authority ROLE_USER
        List<GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
        return id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities);
    }

    /**
     * Creates a {@link UserPrincipalBuilder} for the provided {@link User}, setting its details and additional attributes.
     *
     * @param user       The {@link User} for which to create the {@link UserPrincipalBuilder}.
     * @param attributes A {@link Map} of additional attributes to include in the {@link UserPrincipal}.
     * @return A {@link UserPrincipalBuilder} with the details and attributes of the provided user.
     */
    public UserPrincipalBuilder createUserWithAttributes(User user, Map<String, Object> attributes) {
        return createUser(user)
                .attributes(attributes);
    }
}
