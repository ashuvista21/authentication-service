package com.user.auth.exceptions.auth;

import org.springframework.http.HttpStatus;

import com.user.auth.exceptions.AuthDomainException;

public class InvalidTokenIdentifierException extends AuthDomainException {

	private static final long serialVersionUID = 1L ;
	
	public InvalidTokenIdentifierException(String message) {
		super(message, HttpStatus.UNAUTHORIZED) ;
	}

}
