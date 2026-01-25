package com.user.auth.exceptions;

import org.springframework.http.HttpStatus;

public class DatabaseException extends RuntimeException {
	
	private static final long serialVersionUID = 1L ;
	private final HttpStatus status ;
	
	public DatabaseException(String message, HttpStatus status) {
		super(message) ;
		this.status = status ;
	}
	
	public DatabaseException(String message, HttpStatus status, Throwable cause) {
		super(message, cause) ;
		this.status = status ;
	}
	
	public HttpStatus getStatus() {
		return status ;
	}
}
