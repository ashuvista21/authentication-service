package com.user.auth.entities;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "sessions",
    indexes = {
    		@Index(
    			name = "idx_sessions_user_active",
    			columnList = "user_id"
    	    ),
    		@Index(
                    name = "idx_session_user_device_revoked",
                    columnList = "user_id, device_info"
                )
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Session {

	@Id
    @Column(name = "sid", nullable = false, updatable = false)
    private UUID sid;
	
	@Version
    @Column(nullable = false)
    private Long version;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "device_info")
    private String deviceInfo;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // Use Instant instead of boolean
    // NULL = Active, TIMESTAMP = Revoked
    @Column(name = "revoked_at")
    private Instant revokedAt;

    @PrePersist
    void prePersist() {
        this.createdAt = Instant.now() ;
        this.revokedAt = null ;
    }
}
