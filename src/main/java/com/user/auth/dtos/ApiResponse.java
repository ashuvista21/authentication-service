package com.user.auth.dtos;

import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
	private List<String> message ;
	private boolean success ;
	private HttpStatus status ;
	private T data ;
	
	// ✅ Static factory method for single message
    public static <T> ApiResponse<T> of(String message, boolean success, HttpStatus status, T data) {
        return ApiResponse.<T>builder()
                .message(List.of(message))
                .success(success)
                .status(status)
                .data(data)
                .build();
    }
}
