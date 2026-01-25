package com.user.auth.exceptions.otp;

import org.springframework.http.HttpStatus;

import com.user.auth.exceptions.OTPException;

public class InvalidOtpException extends OTPException {

	private static final long serialVersionUID = 1L;
	
	public InvalidOtpException(String message) {
		super(message, HttpStatus.BAD_REQUEST) ;
	}
	
	public InvalidOtpException(String message, Throwable cause) {
		super(message, HttpStatus.BAD_REQUEST, cause) ;
	}
}
