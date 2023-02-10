package org.eihq.quiltshow.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eihq.quiltshow.model.Person;
import org.eihq.quiltshow.repository.PersonRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JpaUserDetailsService implements UserDetailsService, InitializingBean {
	
	@Autowired
	PersonRepository personRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		List<Person> users = personRepository.findByEmailIgnoreCase(username);
		
		if(users.isEmpty()) {
			throw new UsernameNotFoundException(String.format("No user found with email address %s", username));
		}
		
		UserDetails userDetails = new ShowUserDetails(users.get(0));
		return userDetails;
	}	

	// create the admin account
	@Override
	public void afterPropertiesSet() throws Exception {
		Person admin = new Person();
		admin.setEmail("admin");
		admin.setFirstName("admin");
		admin.setLastName(" ");
		admin.setPassword(passwordEncoder.encode("admin"));
		personRepository.save(admin);
	}
	
	
	class ShowUserDetails implements UserDetails {
		
		private static final long serialVersionUID = 1L;
		
		Person user;

		public ShowUserDetails(Person user) {
			super();
			this.user = user;
		}
		
		public Person getUser() {
			return this.user;
		}

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
			authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
			
			if(user.getEmail().equals("admin")) {
				authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
			}
			
			return authorities;
		}

		@Override
		public String getPassword() {
			return user.getPassword();
		}

		@Override
		public String getUsername() {
			return user.getEmail();
		}

		@Override
		public boolean isAccountNonExpired() {
			return true;
		}

		@Override
		public boolean isAccountNonLocked() {
			return true;
		}

		@Override
		public boolean isCredentialsNonExpired() {
			return true;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}	
		
	}
}
