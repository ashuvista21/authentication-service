package com.user.auth.security.authentication.jwt.adapter;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.user.auth.config.AuthConfigProperties;
import com.user.auth.dtos.ApiResponse;

import lombok.RequiredArgsConstructor;
import validator.contract.IntrospectionPort;
import validator.contract.IntrospectionResponse;

@Component
@RequiredArgsConstructor
public class HttpIntrospectionAdapter implements IntrospectionPort {
	
	private final RestTemplate restTemplate ;
	private final AuthConfigProperties authConfigProperties ;
	
	@Override
	public IntrospectionResponse introspect(String token) {
		
		HttpHeaders headers = new HttpHeaders() ;
		headers.setBearerAuth(token) ;
		
		HttpEntity<Void> request = new HttpEntity<>(headers) ;
		
		ResponseEntity<ApiResponse<com.user.auth.dtos.IntrospectionResponse>> response =
				restTemplate.exchange(
						authConfigProperties.getIntrospectionURL(),
					HttpMethod.POST,
					request,
					new ParameterizedTypeReference<ApiResponse<com.user.auth.dtos.IntrospectionResponse>>() {}
				) ;
		
		return new IntrospectionResponse(response.getBody().getData().isActive()) ;
	}

}
