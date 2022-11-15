package org.eihq.quiltshow.controller;

import org.eihq.quiltshow.service.TokenService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class AuthController {
	
	private final TokenService tokenService;

	public AuthController(TokenService tokenService) {
		this.tokenService = tokenService;
	}

	@PostMapping("/token")
	public String token(Authentication auth) {
		log.debug("Token requested for user {}", auth.getName());
		
		String token = tokenService.generateToken(auth);
		log.debug("Token granted: {}", token);
		return token;
	}

}
