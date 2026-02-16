package com.user.auth.services.impl;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.user.auth.dtos.EmailEvent;
import com.user.auth.entities.EventTypes;
import com.user.auth.entities.OTP;
import com.user.auth.entities.OTPPurpose;
import com.user.auth.exceptions.otp.InvalidOtpException;
import com.user.auth.exceptions.otp.OtpBlockedException;
import com.user.auth.exceptions.otp.OtpExpiredException;
import com.user.auth.repositories.OTPRepository;
import com.user.auth.security.userdetails.CustomUserDetails;
import com.user.auth.security.userdetails.CustomUserDetailsService;
import com.user.auth.services.OTPService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements OTPService {
	
	private final OTPRepository otpRepository ;
	private final PasswordEncoder passwordEncoder ;
	private final CustomUserDetailsService customUserDetailsService ;
	
	private final int MAX_OTP_ATTEMPTS = 3 ;
	
	@Override
	@Transactional
	public EmailEvent generateOTPEmailEventForPasswordReset(String username) {
		
		CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(username) ;
		UUID userId = UUID.fromString(userDetails.getUuid()) ;
		
		String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999)) ;
		String otpHash = passwordEncoder.encode(otp) ;
		
		OTP otpEntity = OTP.builder().userId(userId)
			.otpHash(otpHash)
			.purpose(OTPPurpose.OTP_PASSWORD_RESET)
			.attempts(0)
			.build() ;
		
		otpRepository.save(otpEntity) ;
		
		EmailEvent emailEvent = EmailEvent.builder().eventId(UUID.randomUUID().toString())
			.eventType(EventTypes.OTP.toString())
			.recipient(username)
			.template(OTPPurpose.OTP_PASSWORD_RESET.toString())
			.timestamp(Instant.now())
			.variables(Map.of("name", userDetails.getName(), "otp", otp, "expiryMinutes", 10))
			.build() ;

		return emailEvent ;
	}

	@Override
	@Transactional
	public void validateOTP(String identifier, String code, OTPPurpose purpose) {
		
		CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(identifier) ;
		UUID userId = UUID.fromString(userDetails.getUuid()) ;
		
		OTP otpEntity = otpRepository.findTopByUserIdAndPurposeAndVerifiedAtIsNullOrderByCreatedAtDesc(userId, purpose)
			.orElseThrow(() -> new InvalidOtpException("OTP not found")) ;
		
		if (otpEntity.isExpired()) {
		    throw new OtpExpiredException("OTP expired") ; //410
		}
		
		if (otpEntity.getAttempts() >= MAX_OTP_ATTEMPTS) {
	        throw new OtpBlockedException("Too many attempts") ; //429
	    }

		if (!passwordEncoder.matches(code, otpEntity.getOtpHash())) {
		    otpEntity.setAttempts(otpEntity.getAttempts() + 1) ;
		    //removing below line since method is under @Transactional,
		    //so hibernate will do dirty checking,
		    //and it finds changes in the entity and update the same in db also
		    //below is what after @Transactional boundary ends
		    /*
		     *	Transaction ends →
			 *  Flush happens →
			 *  Hibernate compares old vs new state →
			 *  Generates UPDATE query →
			 *  Commits
		     */
		    //otpRepository.save(otpEntity) ;
		    throw new InvalidOtpException("Invalid OTP") ; //400
		}
		
		otpRepository.markVerified(otpEntity.getId(), Instant.now()) ;
	}

}
