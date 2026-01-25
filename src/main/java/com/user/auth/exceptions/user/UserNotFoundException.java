package com.user.auth.exceptions.user;

import org.springframework.http.HttpStatus;

import com.user.auth.exceptions.UserException;

public class UserNotFoundException extends UserException {
	
	private static final long serialVersionUID = 1L;
	
	public UserNotFoundException(String message) {
		super(message, HttpStatus.NOT_FOUND) ;
	}
}
