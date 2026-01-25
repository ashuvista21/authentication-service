package com.user.auth.exceptions.otp;

import org.springframework.http.HttpStatus;

import com.user.auth.exceptions.OTPException;

public class OtpBlockedException extends OTPException {

	private static final long serialVersionUID = 1L;
	
	public OtpBlockedException(String message) {
		super(message, HttpStatus.BAD_REQUEST) ;
	}
	
	public OtpBlockedException(String message, Throwable cause) {
		super(message, HttpStatus.BAD_REQUEST, cause) ;
	}
}
