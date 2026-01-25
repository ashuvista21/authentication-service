package com.user.auth.controllers;

import java.util.Arrays;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.user.auth.config.JwtProperties;
import com.user.auth.dtos.ApiResponse;
import com.user.auth.dtos.JwkSet;
import com.user.auth.dtos.JwtResponse;
import com.user.auth.dtos.LoginRequest;
import com.user.auth.entities.GrantTypes;
import com.user.auth.security.authentication.service.AuthService;
import com.user.auth.validation.PasswordFlow;
import com.user.auth.validation.RefreshTokenFlow;

import org.springframework.data.util.Pair;

import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
	
	private final AuthService authService;
	private final Validator validator ;
	private final JwtProperties jwtProperties ;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest request) {
    	if(request.getGrantType().equals(GrantTypes.PASSWORD)) {
    		validator.validate(request, PasswordFlow.class) ;
    	}
    	else if(request.getGrantType().equals(GrantTypes.REFRESH_TOKEN)) {
    		validator.validate(request, RefreshTokenFlow.class) ;
    	}
    	else {
    		throw new IllegalArgumentException("Unsupported grant type: " + request.getGrantType()) ;
    	}
    	
        Pair<String, String> tokens = authService.login(request, jwtProperties.algorithm()) ;
        
        return ResponseEntity.ok(
            ApiResponse.<JwtResponse>builder()
                .message(Arrays.asList("Login successful"))
                .success(true)
                .status(HttpStatus.OK)
                .data(JwtResponse.builder()
                		.token(tokens.getFirst())
                		.refreshToken(tokens.getSecond())
                		.build())
                .build()
        );
    }

    @GetMapping("/validate-token")
    public ResponseEntity<ApiResponse<Claims>> validateToken(@RequestParam String token) {
        boolean isValid = authService.verifyToken(token);

        if (isValid) {
            return ResponseEntity.ok(ApiResponse.<Claims>builder()
                    .success(true)
                    .status(HttpStatus.OK)
                    .message(Arrays.asList("Token is valid"))
                    .data(authService.getClaims(token))
                    .build());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<Claims>builder()
                            .success(false)
                            .status(HttpStatus.UNAUTHORIZED)
                            .message(Arrays.asList("Invalid or expired token"))
                            .data(null)
                            .build());
        }
    }
    
    @GetMapping("/.well-known/jwks.json")
    public ResponseEntity<JwkSet> getJwks() {
        return ResponseEntity.ok(authService.getRSAPublicKey(jwtProperties.algorithm())) ;
    }
}
