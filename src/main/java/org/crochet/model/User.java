package org.crochet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Email
  @Column(nullable = false)
  private String email;

  private String imageUrl;

  @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
  private Boolean emailVerified;

  @JsonIgnore
  private String password;

  @NotNull
  @Enumerated(EnumType.STRING)
  private AuthProvider provider;

  private String providerId;

  private String verificationCode;

  @NotNull
  @Enumerated(EnumType.STRING)
  private RoleType role;

  @OneToMany(mappedBy = "user")
  private Set<ConfirmationToken> confirmationTokens;

  @OneToMany(mappedBy = "user")
  private Set<PasswordResetToken> passwordResetTokens;

  @OneToMany(mappedBy = "user")
  private Set<Order> orders;
}
