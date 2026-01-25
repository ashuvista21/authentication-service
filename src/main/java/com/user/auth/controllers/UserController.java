package com.user.auth.controllers;

import java.util.Arrays;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.user.auth.dtos.ApiResponse;
import com.user.auth.dtos.LogoutScope;
import com.user.auth.security.authentication.jwt.AuthPrincipal;
import com.user.auth.security.authentication.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
	
	private final AuthService authService;

	@GetMapping("/ping")
	public ResponseEntity<ApiResponse<Void>> ping() {
		return ResponseEntity.ok(ApiResponse.<Void>builder()
				.message(Arrays.asList("Hello"))
				.success(true)
				.status(HttpStatus.OK)
				.build()) ;
	}
	
	@GetMapping("/password/modify")
	public ResponseEntity<ApiResponse<Void>> modifyPassowrd() {
		return ResponseEntity.ok(ApiResponse.<Void>builder()
				.message(Arrays.asList("Hello"))
				.success(true)
				.status(HttpStatus.OK)
				.build()) ;
	}
	
	@GetMapping("/account/enable")
	public ResponseEntity<ApiResponse<Void>> enable() {
		return ResponseEntity.ok(ApiResponse.<Void>builder()
				.message(Arrays.asList("Hello"))
				.success(true)
				.status(HttpStatus.OK)
				.build()) ;
	}
	
	@GetMapping("/account/disable")
	public ResponseEntity<ApiResponse<Void>> disable() {
		return ResponseEntity.ok(ApiResponse.<Void>builder()
				.message(Arrays.asList("Hello"))
				.success(true)
				.status(HttpStatus.OK)
				.build()) ;
	}
	
	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Integer>> logout(@RequestParam(defaultValue = "CURRENT") LogoutScope scope, @AuthenticationPrincipal AuthPrincipal principal) {
		Integer numberOfLogouts = authService.logout(scope, principal.getSid()) ;
		
		return ResponseEntity.ok(ApiResponse.<Integer>builder()
				.message(Arrays.asList("Logout Successfull"))
				.success(true)
				.status(HttpStatus.OK)
				.data(numberOfLogouts)
				.build()) ;
	}
}
