package org.crochet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter@Setter
@Entity
@Table(name = "token_blacklist")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TokenBlacklist extends BaseEntity {
    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "blacklisted_at")
    private LocalDateTime blacklistedAt;
}
