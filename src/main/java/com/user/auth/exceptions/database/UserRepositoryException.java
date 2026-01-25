package com.user.auth.exceptions.database;

import org.springframework.http.HttpStatus;

import com.user.auth.exceptions.DatabaseException;

public class UserRepositoryException extends DatabaseException {

	private static final long serialVersionUID = 1L ;
	
	public UserRepositoryException(String message) {
		super(message, HttpStatus.INTERNAL_SERVER_ERROR) ;
	}

}
