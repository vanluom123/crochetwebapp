package org.crochet.security;

import lombok.Getter;
import org.crochet.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * UserPrincipal class
 */
public class UserPrincipal implements OAuth2User, UserDetails {
  @Getter
  private Long id;
  private String name;
  private String email;
  private String password;
  private Collection<? extends GrantedAuthority> authorities;
  private Map<String, Object> attributes;

  /**
   * Constructor of UserPrincipal class
   *
   * @param id id
   * @param email email
   * @param password password
   * @param authorities authorities
   */
  public UserPrincipal(Long id,
                       String name,
                       String email,
                       String password,
                       Collection<? extends GrantedAuthority> authorities) {
    this.id = id;
    this.email = email;
    this.password = password;
    this.authorities = authorities;
  }

  /**
   * Creates a UserPrincipal object from the provided User object.
   *
   * @param user The User object to create the UserPrincipal from.
   * @return The created UserPrincipal object.
   */
  public static UserPrincipal create(User user) {
    // Create a list of GrantedAuthority with a single authority ROLE_USER
    List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

    // Create and return a new UserPrincipal object with the provided user's details and authorities
    return new UserPrincipal(
        user.getId(),
        user.getName(),
        user.getEmail(),
        user.getPassword(),
        authorities
    );
  }

  /**
   * Create object
   *
   * @param user User
   * @param attributes Map<String, Object>
   * @return UserPrincipal
   */
  public static UserPrincipal create(User user, Map<String, Object> attributes) {
    UserPrincipal userPrincipal = UserPrincipal.create(user);
    userPrincipal.setAttributes(attributes);
    return userPrincipal;
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

  public void setAttributes(Map<String, Object> attributes) {
    this.attributes = attributes;
  }

  @Override
  public String getName() {
    return this.name;
  }
}
