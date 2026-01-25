package com.user.auth.exceptions.auth;

import org.springframework.http.HttpStatus;

import com.user.auth.exceptions.AuthDomainException;

public class InvalidTokenException extends AuthDomainException {
	
	private static final long serialVersionUID = 1L ;
	
	public InvalidTokenException(String message) {
		super(message, HttpStatus.UNAUTHORIZED) ;
	}
}
