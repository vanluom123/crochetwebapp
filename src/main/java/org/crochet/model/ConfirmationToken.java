package org.crochet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "confirmation_token")
@NoArgsConstructor
@Accessors(chain = true)
public class ConfirmationToken extends BaseEntity {
    @Column(name = "token",
            unique = true,
            nullable = false,
            updatable = false)
    private String token;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
