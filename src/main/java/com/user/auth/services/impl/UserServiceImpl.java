package com.user.auth.services.impl;

import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.user.auth.dtos.SignupRequest;
import com.user.auth.entities.User;
import com.user.auth.exceptions.database.UserRepositoryException;
import com.user.auth.exceptions.user.InvalidCredentialsException;
import com.user.auth.exceptions.user.InvalidUserInputException;
import com.user.auth.exceptions.user.UserAlreadyExistsException;
import com.user.auth.exceptions.user.UserNotFoundException;
import com.user.auth.repositories.UserRepository;
import com.user.auth.services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	
	private final UserRepository userRepository ;
	private final PasswordEncoder passwordEncoder ;

	@Override
	public User saveUser(SignupRequest request) {
		if (userRepository.existsByUsername(request.getUsername())) {
	        throw new UserAlreadyExistsException("Username already taken: " + request.getUsername());
	    }

	    User user = User.builder()
	        .uuid(UUID.randomUUID().toString())
	        .username(request.getUsername())
	        .password(passwordEncoder.encode(request.getPassword()))
	        .role(request.getRole())
	        .name(request.getName())
	        .build();

	    try {
	        return userRepository.save(user);
	    } catch (DataIntegrityViolationException e) {
	        throw new UserRepositoryException("Could not save user") ;
	    }
		
	}

	@Override
	public User getUser(String userId) {
		if (userId == null || userId.isBlank()) {
	        throw new InvalidUserInputException("User ID cannot be null or blank") ;
	    }
		
	    return userRepository.findById(userId)
	            .orElseThrow(() -> new UserNotFoundException("User not found")) ;
	}

	@Override
	public void changePassword(String userId, String oldPassword, String newPassword) {
		User user = getUser(userId);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Old password does not match");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
	}

	@Override
	public void resetPassword(String userId, String newPassword) {
		User user = getUser(userId) ;
		user.setPassword(passwordEncoder.encode(newPassword)) ;
		userRepository.save(user) ;
	}

	@Override
	public void updateUserStatus(String userId, boolean enabled) {
		User user = getUser(userId) ;
		user.setEnabled(enabled) ;
	    userRepository.save(user) ;
	}

}
