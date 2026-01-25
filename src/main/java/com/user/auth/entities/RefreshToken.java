package com.user.auth.entities;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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

@Entity
@Table(
    name = "refresh_tokens",
    indexes = {
        @Index(name = "idx_refresh_tokens_token", columnList = "refresh_token", unique = true),
        @Index(name = "idx_refresh_tokens_sid", columnList = "sid")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	/** Optimistic locking column */
    @Version
    @Column(nullable = false)
    private Long version;

    /** Always session-bound */
    @Column(name = "sid")
    private UUID sid;

    @Column(name = "refresh_token", nullable = false)
    private UUID refreshToken;

    @Column(name = "issued_at", nullable = false, updatable = false)
    private Instant issuedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    // Use Instant instead of boolean
    // NULL = Active, TIMESTAMP = Revoked
    @Column(name = "revoked_at")
    private Instant revokedAt;

    @PrePersist
    void prePersist() {
    	if(this.issuedAt == null) {
    		this.issuedAt = Instant.now() ;
        	this.revokedAt = null ;
    	}
    }
    
    /**
     * Rotate refresh token:
     * - invalidate old token value
     * - generate new token value
     * - reset issuedAt & expiresAt
     *
     * IMPORTANT:
     * - sid is NOT changed
     * - entity row is reused
     */
    public void rotate(Duration refreshTtl) {
        Instant now = Instant.now() ;

        this.refreshToken = UUID.randomUUID() ; // new token value
        this.issuedAt = now ;
        this.expiresAt = now.plus(refreshTtl) ;
        this.revokedAt = null ;
    }

    /**
     * Explicit revocation (logout / security event)
     */
    public void revoke() {
        this.revokedAt = Instant.now() ;
    }
}
