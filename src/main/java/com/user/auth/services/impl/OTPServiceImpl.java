package com.user.auth.services.impl;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.user.auth.dtos.EmailEvent;
import com.user.auth.entities.OTP;
import com.user.auth.entities.OTPPurpose;
import com.user.auth.exceptions.otp.InvalidOtpException;
import com.user.auth.exceptions.otp.OtpBlockedException;
import com.user.auth.exceptions.otp.OtpExpiredException;
import com.user.auth.repositories.OTPRepository;
import com.user.auth.security.userdetails.CustomUserDetails;
import com.user.auth.security.userdetails.CustomUserDetailsService;
import com.user.auth.services.OTPService;

import jakarta.transaction.Transactional;
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
			.purpose(OTPPurpose.PASSWORD_RESET)
			.attempts(0)
			.build() ;
		
		otpRepository.save(otpEntity) ;
		
		EmailEvent emailEvent = EmailEvent.builder().eventId(UUID.randomUUID().toString())
			.eventType(OTPPurpose.PASSWORD_RESET.toString())
			.recipient(username)
			.template("OTP_SENT")
			.timestamp(Instant.now())
			.variables(Map.of("OTP", otp))
			.build() ;

		return emailEvent ;
	}

	@Override
	@Transactional
	public void validateOTP(String identifier, String code, OTPPurpose purpose) {
		
		CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(identifier) ;
		UUID userId = UUID.fromString(userDetails.getUuid()) ;
		
		OTP otpEntity = otpRepository.findTopByUserIdAndPurposeAndVerifiedAtIsNullOrderByCreatedAtDesc(userId, purpose)
			.orElseThrow(() -> new IllegalStateException("OTP not found")) ;
		
		if (otpEntity.isExpired()) {
		    throw new OtpExpiredException("OTP expired") ; //410
		}
		
		if (otpEntity.getAttempts() >= MAX_OTP_ATTEMPTS) {
	        throw new OtpBlockedException("Too many attempts") ; //429
	    }

		if (!passwordEncoder.matches(code, otpEntity.getOtpHash())) {
		    otpEntity.setAttempts(otpEntity.getAttempts() + 1) ;
		    otpRepository.save(otpEntity) ;
		    throw new InvalidOtpException("Invalid OTP") ; //400
		}
		
		otpRepository.markVerified(otpEntity.getId(), Instant.now()) ;
	}

}
