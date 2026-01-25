package com.user.auth.security.authentication.jwt;

public record JwtMetadata(
		String jti,
		String sid
		) {}
