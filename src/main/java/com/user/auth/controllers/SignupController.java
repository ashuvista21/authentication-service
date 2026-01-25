package com.user.auth.controllers;

import java.util.Arrays;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.auth.dtos.ApiResponse;
import com.user.auth.dtos.SignupRequest;
import com.user.auth.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/signup")
public class SignupController {
	
	private final UserService userService ;
	
	@PostMapping("/register")
	public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequest request) {
	    userService.saveUser(request);
	    return ResponseEntity.status(HttpStatus.CREATED)
	        .body(ApiResponse.<Void>builder()
	            .message(Arrays.asList("User registered successfully"))
	            .success(true)
	            .status(HttpStatus.CREATED)
	            .build());
	}

}
