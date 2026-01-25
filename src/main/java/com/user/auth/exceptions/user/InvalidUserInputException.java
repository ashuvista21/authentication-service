package com.user.auth.exceptions.user;

import org.springframework.http.HttpStatus;

import com.user.auth.exceptions.UserException;

public class InvalidUserInputException extends UserException {

	private static final long serialVersionUID = 1L ;
	
	public InvalidUserInputException(String message) {
		super(message, HttpStatus.BAD_REQUEST) ;
	}

}
