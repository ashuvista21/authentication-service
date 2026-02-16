package com.user.auth.controllers;

import java.util.Arrays;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.auth.dtos.ApiResponse;
import com.user.auth.dtos.EmailEvent;
import com.user.auth.dtos.OTPVerifyRequest;
import com.user.auth.dtos.PasswordResetRequest;
import com.user.auth.entities.OTPPurpose;
import com.user.auth.security.userdetails.CustomUserDetails;
import com.user.auth.security.userdetails.CustomUserDetailsService;
import com.user.auth.services.OTPService;
import com.user.auth.services.ProducerService;
import com.user.auth.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/password")
public class PasswordResetController {
	
	private final OTPService otpService ;
	private final ProducerService producerService ;
	private final UserService userService ;
	private final CustomUserDetailsService userDetailsService ;

	@PostMapping("/reset")
	public ResponseEntity<ApiResponse<Void>> passwordResetInitiate(@Valid @RequestBody PasswordResetRequest emailRequest) {
		
		EmailEvent emailEvent = otpService.generateOTPEmailEventForPasswordReset(emailRequest.recipient()) ;
		
		producerService.send(emailEvent) ;
		
		return ResponseEntity.ok(
				ApiResponse.<Void>builder()
				.message(Arrays.asList("OTP sent successfully"))
                .success(true)
                .status(HttpStatus.OK)
                .build()
		) ;
	}
	
	@PostMapping("/veify")
	public ResponseEntity<ApiResponse<Void>> passwordVerifyOTP(@Valid @RequestBody OTPVerifyRequest otpVerifyRequest) {
		
		otpService.validateOTP(otpVerifyRequest.identifier(), otpVerifyRequest.otp(), OTPPurpose.OTP_PASSWORD_RESET) ;
		
		CustomUserDetails userByUsername = userDetailsService.loadUserByUsername(otpVerifyRequest.identifier()) ;
		userService.resetPassword(userByUsername.getUuid(), otpVerifyRequest.newPassword()) ;
		
		return ResponseEntity.ok(
				ApiResponse.<Void>builder()
				.message(Arrays.asList("Password resets successfully"))
                .success(true)
                .status(HttpStatus.OK)
                .build()
		) ;
	}
}
