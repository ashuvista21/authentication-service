package com.user.auth.repositories;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.user.auth.entities.OTP;
import com.user.auth.entities.OTPPurpose;

@Repository
public interface OTPRepository extends JpaRepository<OTP, UUID>{
	
	@Modifying
    @Query("""
        update OTP o
           set o.verifiedAt = :now
         where o.id = :id
    """)
    int markVerified(UUID id, Instant now) ;
	
	Optional<OTP> findTopByUserIdAndPurposeAndVerifiedAtIsNullOrderByCreatedAtDesc(
			UUID userId, OTPPurpose purpose) ;

}
