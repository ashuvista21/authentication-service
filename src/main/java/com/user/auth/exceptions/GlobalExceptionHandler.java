package com.user.auth.exceptions;

import java.util.Arrays;
import java.util.List;

import org.apache.kafka.common.KafkaException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.user.auth.dtos.ApiResponse;

import validator.exceptions.JwtValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserExists(UserException ex) {
        return buildResponse(ex.getMessage(), ex.getStatus()) ;
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleDatabaseError(DatabaseException ex) {
        return buildResponse("Internal error occurred", ex.getStatus()) ;
    }
    
    @ExceptionHandler(OTPException.class)
    public ResponseEntity<ApiResponse<Void>> handleBlockedOTP(OTPException ex) {
		return buildResponse(ex.getMessage(), ex.getStatus()) ;   	
    }
    
    @ExceptionHandler(AuthDomainException.class)
    public ResponseEntity<ApiResponse<Void>> handleDeviceIdMismatch(AuthDomainException ex) {
    	return buildResponse(ex.getMessage(), ex.getStatus()) ;
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(
            AuthenticationException ex) {

        HttpStatus status ;
        String message ;
        
        if(ex instanceof UsernameNotFoundException) {
        	status = HttpStatus.UNAUTHORIZED ;
            message = "Username not found" ;
            
        } else if (ex instanceof BadCredentialsException) {
            status = HttpStatus.UNAUTHORIZED ;
            message = "Invalid username or password" ;

        } else if (ex instanceof AuthenticationCredentialsNotFoundException) {
            status = HttpStatus.UNAUTHORIZED ;
            message = "Authentication credentials not found" ;

        } else if (ex instanceof DisabledException) {
            status = HttpStatus.FORBIDDEN ;
            message = "User account is disabled" ;

        } else if (ex instanceof LockedException) {
            status = HttpStatus.FORBIDDEN ;
            message = "User account is locked" ;

        } else if (ex instanceof AccountExpiredException) {
            status = HttpStatus.FORBIDDEN ;
            message = "User account has expired" ;

        } else if (ex instanceof CredentialsExpiredException) {
            status = HttpStatus.UNAUTHORIZED ;
            message = "User credentials have expired" ;

        } else {
            status = HttpStatus.UNAUTHORIZED ;
            message = "Authentication failed" ;
        }

        return buildResponse(message, status) ;
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleArguementsNotValid(MethodArgumentNotValidException ex) {
    	return buildResponse(ex.getBindingResult().getFieldErrors().stream()
    			.map(DefaultMessageSourceResolvable::getDefaultMessage)
    			.toList(), HttpStatus.BAD_REQUEST) ;
    }
    
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFound(NoResourceFoundException ex) {
    	return buildResponse("The requested endpoint '" + ex.getResourcePath() + "' does not exist", HttpStatus.NOT_FOUND) ;
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
    	//return buildResponse("Invalid request payload: " + ex.getMostSpecificCause().getMessage(), HttpStatus.BAD_REQUEST) ;
    	return buildResponse("Invalid Request Payload", HttpStatus.BAD_REQUEST) ;
    }
    
    @ExceptionHandler(JwtValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleKafkaErrors(JwtValidationException ex) {
    	
    	HttpStatus status = switch (ex.getErrorCode()) {
    		case EXPIRED -> HttpStatus.UNAUTHORIZED ;
    		case INVALID_SIGNATURE -> HttpStatus.UNAUTHORIZED ;
    		case NOT_YET_VALID -> HttpStatus.UNAUTHORIZED ;
    		case MALFORMED -> HttpStatus.UNAUTHORIZED ;
    		default -> HttpStatus.UNAUTHORIZED ;
    	} ;
    	
    	return buildResponse("Unexpected error: " + ex.getMessage(), status) ;
    }
    
    @ExceptionHandler(KafkaException.class)
    public ResponseEntity<ApiResponse<Void>> handleKafkaErrors(KafkaException ex) {
    	return buildResponse("Unexpected error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR) ;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleOtherErrors(Exception ex) {
        return buildResponse("Unexpected error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR) ;
    }

    // ✅ Common builder method to keep DRY
    private ResponseEntity<ApiResponse<Void>> buildResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status)
            .body(ApiResponse.<Void>builder()
                .message(Arrays.asList(message))
                .success(false)
                .status(status)
                .build()) ;
    }
    
    private ResponseEntity<ApiResponse<Void>> buildResponse(List<String> messages, HttpStatus status) {
    	if(messages.size() == 0)
    		return buildResponse("Validation Error", status) ;
        return ResponseEntity.status(status)
            .body(ApiResponse.<Void>builder()
                .message(messages)
                .success(false)
                .status(status)
                .build()) ;
    }
}
