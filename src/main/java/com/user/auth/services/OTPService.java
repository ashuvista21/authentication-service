package com.user.auth.services;

import com.user.auth.dtos.EmailEvent;
import com.user.auth.entities.OTPPurpose;

public interface OTPService {
	EmailEvent generateOTPEmailEventForPasswordReset(String username) ;
	void validateOTP(String identifier, String code, OTPPurpose purpose) ;
}
