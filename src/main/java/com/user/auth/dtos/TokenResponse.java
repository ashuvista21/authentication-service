package com.user.auth.dtos;

public record TokenResponse(
		String accessTokenJti,
		String refreshToken)
{} ;