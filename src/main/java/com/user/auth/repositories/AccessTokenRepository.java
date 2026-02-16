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

import com.user.auth.entities.AccessToken;

@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, UUID> {
	
	@Modifying(clearAutomatically = true)
    @Query("""
        UPDATE AccessToken a
           SET a.revokedAt = :revokedAt
         WHERE a.sid IN :sids
           AND a.revokedAt IS NULL
    """)
    int revokeActiveBySids(@Param("sids") List<UUID> sids, @Param("revokedAt") Instant revokedAt);
	
	Optional<AccessToken> findBySidAndRevokedAtIsNull(UUID sid) ;
	
	Optional<AccessToken> findBySid(UUID sid) ;
}
