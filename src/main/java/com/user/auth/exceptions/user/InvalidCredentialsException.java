package com.user.auth.exceptions.user;

import org.springframework.http.HttpStatus;

import com.user.auth.exceptions.UserException;

public class InvalidCredentialsException extends UserException {

	private static final long serialVersionUID = 1L ;
	
	public InvalidCredentialsException(String message) {
		super(message, HttpStatus.BAD_REQUEST) ;
	}

}
