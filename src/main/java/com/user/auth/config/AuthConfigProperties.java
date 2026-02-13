package com.user.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Setter;

@Setter
@ConfigurationProperties(prefix = "auth")
public class AuthConfigProperties {
	private String introspectionDomainName ;
	private String introspectionEndpoint ;
	private long maxActiveSessions ;
	private boolean externalIntrospectionFlag ;
	
	public String getIntrospectionURL() {
		return this.introspectionDomainName + this.introspectionEndpoint ;
	}
	
	public long getMaxActiveSessions() {
		return this.maxActiveSessions ;
	}
	
	public boolean useExternalIntrospection() {
		return this.externalIntrospectionFlag ;
	}
}
