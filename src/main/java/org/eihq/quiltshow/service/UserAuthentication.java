package org.eihq.quiltshow.service;

import java.util.stream.Stream;

import org.eihq.quiltshow.configuration.UserRoles;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class UserAuthentication {
		

	public Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}
	
	public boolean hasRole(UserRoles role) {
		if(role == null) {
			return false;
		}
		
		Object principal = getAuthentication().getPrincipal();
		if(principal instanceof Jwt) {
			Jwt jwt = (Jwt)principal;
			return Stream.of(jwt.getClaimAsString("scope").split(" ")).anyMatch(c -> c.equals(role.toString()));
		}
		else if(principal instanceof UserDetails) {
			UserDetails currentUser = (UserDetails)principal;			
			return currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role.toString()));
		}
		
		return false;
	}
}
