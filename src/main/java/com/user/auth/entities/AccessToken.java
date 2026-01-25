package com.user.auth.entities;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
//change DB FROM MARIA DB TO POSTGRES DB
@Entity
@Table(
    name = "access_tokens",
    indexes = {
        @Index(name = "idx_access_tokens_sid", columnList = "sid")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessToken {
	/** JWT ID – unique per access token */
    @Id
    @Column(name = "jti", nullable = false, updatable = false)
    private UUID jti;
    
    @Version
    @Column(nullable = false)
    private Long version;

    /**
     * Session ID
     * - NULL for service-to-service tokens
     * - NOT NULL for user tokens
     */
    @Column(name = "sid")
    private UUID sid;

    /**
     * userId (UUID as string) OR service-name
     */
    @Column(name = "subject", nullable = false, length = 128)
    private String subject;

    @Column(name = "issued_at", nullable = false, updatable = false)
    private Instant issuedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    // Use Instant instead of boolean
    // NULL = Active, TIMESTAMP = Revoked
    @Column(name = "revoked_at")
    private Instant revokedAt ;

    @PrePersist
    void prePersist() {
        this.issuedAt = Instant.now();
        this.revokedAt = null;
    }
}
