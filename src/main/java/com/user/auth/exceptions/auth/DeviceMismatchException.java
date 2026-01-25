package com.user.auth.exceptions.auth;

import org.springframework.http.HttpStatus;

import com.user.auth.exceptions.AuthDomainException;

public class DeviceMismatchException extends AuthDomainException {

	private static final long serialVersionUID = 1L ;

	public DeviceMismatchException(String message) {
        super(message, HttpStatus.UNAUTHORIZED) ;
    }
}
