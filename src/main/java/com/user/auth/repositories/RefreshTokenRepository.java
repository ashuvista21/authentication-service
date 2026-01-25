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

import com.user.auth.entities.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByRefreshTokenAndRevokedAtIsNull(UUID refreshToken) ;
    Optional<RefreshToken> findBySidAndRevokedAtIsNull(UUID sid) ;
    Optional<RefreshToken> findByRefreshToken(UUID refreshToken) ;
    
    @Modifying
    @Query("""
     update RefreshToken r
        set r.revokedAt = :now
      where r.sid in :sids
        and r.revokedAt is null
    """)
    int revokeBySid(@Param("sids") List<UUID> sids, @Param("now") Instant now) ;
}
