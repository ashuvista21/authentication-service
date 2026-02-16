package com.user.auth.repositories;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.user.auth.entities.Session;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {
	/**
     * Check if an active session already exists for the same user and device
     */
    Optional<Session> findByUserIdAndDeviceInfoAndRevokedAtIsNull(
            UUID userId,
            String deviceInfo
    );

    /**
     * Count active sessions for enforcing max session limit
     */
    @Query("""
        SELECT COUNT(s)
        FROM Session s
        WHERE s.userId = :userId
          AND s.revokedAt IS NULL
    """)
    long countActiveSessions(@Param("userId") UUID userId) ;
    
    @Query("""
    	    SELECT s.sid
    	    FROM Session s
    	    WHERE s.userId = :userId
    	      AND s.revokedAt IS NULL
    	""")
    List<UUID> findActiveSessionSidsByUserId(@Param("userId") UUID userId) ;
    
    @Modifying(clearAutomatically = true)
    @Query("""
     update Session s
        set s.revokedAt = :now
      where s.userId = :userId
        and s.revokedAt is null
    """)
    int revokeByUserId(@Param("userId") UUID userId, @Param("now") Instant now) ;
}
