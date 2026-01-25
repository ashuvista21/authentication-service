package com.user.auth.services;

import com.user.auth.dtos.SignupRequest;
import com.user.auth.entities.User;

public interface UserService {
	
	User saveUser(SignupRequest request) ;
	User getUser(String userId) ;
	
	// 🔑 Account Management
    void changePassword(String userId, String oldPassword, String newPassword) ;
    void resetPassword(String userId, String newPassword) ;
    void updateUserStatus(String userId, boolean enabled) ;
}
