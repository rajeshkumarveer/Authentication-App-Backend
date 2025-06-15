package com.AlNada.config;

import java.time.Instant;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.AlNada.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AuthInterceptor implements HandlerInterceptor {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);

	@Autowired private AuthService authService;
	 public AuthInterceptor(AuthService authService) {
	        this.authService = authService;
	    }
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

		try {
			String requestUri = request.getRequestURI();
			if (requestUri.contains("/api/auth/login") || requestUri.contains("/api/auth/register")) {
				return true;
			}
			String authHeader = request.getHeader("Authorization");
			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				String jwtToken = authHeader.substring(7); // Get token only by Remove "Bearer " prefix
				try {
					//check valid auth user or not....
					authService.getUser(jwtToken);
				 }catch(Exception e) {
					 response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					return false;
				 }
				
				// You can now use check jwtToken's session valid or expired..... 
				byte[] decodedBytes = Base64.getDecoder()
						.decode(jwtToken.substring(jwtToken.indexOf('.') + 1, jwtToken.lastIndexOf('.')));

				// Create an ObjectMapper instance
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode jsonNode;

				jsonNode = objectMapper.readTree(new String(decodedBytes));

				JsonNode tokenExp = jsonNode.get("exp");

				Long currentTime = Instant.now().getEpochSecond();
				if (Long.parseLong(tokenExp.asText()) > currentTime) {
					//session valid
					return true;
				} else {
					//session expired
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					return false;
				}
			}
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage());
			return false;
		}
	}
}
