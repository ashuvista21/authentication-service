package com.user.auth.exceptions.auth;

import org.springframework.http.HttpStatus;

import com.user.auth.exceptions.AuthDomainException;

public class MaximumLimitReachedException extends AuthDomainException {

	private static final long serialVersionUID = 1L ;

	public MaximumLimitReachedException(String message) {
		super(message, HttpStatus.TOO_MANY_REQUESTS) ;
	}
}
