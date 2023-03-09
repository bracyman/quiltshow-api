package org.eihq.quiltshow.configuration;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum UserRoles {

	ROLE_USER("ROLE_USER"), ROLE_ADMIN("ROLE_ADMIN");
	
	GrantedAuthority authority;
	
	private UserRoles(String auth) {
		authority = new SimpleGrantedAuthority(auth);
	}
	
	public static UserRoles from(String s) {
		return valueOf(s);
	}
	
	public GrantedAuthority getAuthority() {
		return this.authority;
	}
}
