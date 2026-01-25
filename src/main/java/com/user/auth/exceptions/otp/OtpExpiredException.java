package com.user.auth.exceptions.otp;

import org.springframework.http.HttpStatus;

import com.user.auth.exceptions.OTPException;

public class OtpExpiredException extends OTPException {

	private static final long serialVersionUID = 1L;
	
	public OtpExpiredException(String message) {
		super(message, HttpStatus.GONE) ;
	}
	
	public OtpExpiredException(String message, Throwable cause) {
		super(message, HttpStatus.GONE, cause) ;
	}
}
