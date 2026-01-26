package com.user.auth.exceptions.auth;

import org.springframework.http.HttpStatus;

import com.user.auth.exceptions.AuthDomainException;

public class InvalidRequestPayloadException extends AuthDomainException {

	private static final long serialVersionUID = 1L ;

	public InvalidRequestPayloadException(String message) {
		super(message, HttpStatus.BAD_REQUEST) ;
	}
}
