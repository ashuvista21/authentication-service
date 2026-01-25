package com.user.auth.exceptions.user;

import org.springframework.http.HttpStatus;

import com.user.auth.exceptions.UserException;

public class UserAlreadyExistsException extends UserException {
	private static final long serialVersionUID = 1L ;
	
	public UserAlreadyExistsException(String message) {
		super(message, HttpStatus.CONFLICT) ;
	}
}
