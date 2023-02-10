package org.eihq.quiltshow.service;

import org.eihq.quiltshow.configuration.UserRoles;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
		
		UserDetails currentUser = (UserDetails)getAuthentication().getPrincipal();
		
		return currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role.toString()));
	}
}
