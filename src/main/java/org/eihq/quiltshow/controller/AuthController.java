package org.eihq.quiltshow.controller;

import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import javax.security.auth.login.AccountNotFoundException;

import org.eihq.quiltshow.configuration.UserRoles;
import org.eihq.quiltshow.model.Person;
import org.eihq.quiltshow.service.PersonService;
import org.eihq.quiltshow.service.TokenService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@Slf4j
public class AuthController implements InitializingBean {

	private static final String DEFAULT_ADMIN = "admin";
	
	@Value("${application.environment:dev}")
	String environment;


	@Autowired
	TokenService tokenService;

	@Autowired
	PersonService personService;
	
	@Autowired
	PasswordEncoder passwordEncoder;

	@GetMapping("/verify/{email}")
	public boolean verify(@PathVariable String email) {
		log.debug("Verifying user {}", email);

		boolean exists = null != personService.getUser(email);
		
		return exists;
	}

	@PostMapping("/register")
	public TokenResponse register(@RequestBody Person userData) throws URISyntaxException {
		log.debug("Registering user {}", userData.getEmail());

		List<GrantedAuthority> authorities = userData.getRoles().stream().map(r -> r.getAuthority()).collect(Collectors.toList());
		
		// add default password
		userData.setPassword(userData.getPassword() == null ? "iowastar23" : userData.getPassword());	
		String encodedPassword = passwordEncoder.encode(userData.getPassword());
		userData.setPassword(encodedPassword);		
		Person newUser = personService.save(userData);
		org.springframework.security.core.userdetails.User userDetails 
			= new org.springframework.security.core.userdetails.User(newUser.getEmail(), newUser.getPassword(), authorities);
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String token = tokenService.generateToken(authentication);
		log.debug("Token granted: {}", token);
		
		return new TokenResponse(newUser, token);
	}

	@PostMapping("/token")
	public TokenResponse token(Authentication auth) throws AccountNotFoundException {
		log.info("Token requested for user {}", auth.getName());

		String token = tokenService.generateToken(auth);
		log.debug("Token granted: {}", token);
		
		Person usr = personService.getUser(auth.getName());
		if(usr == null) {
			throw new AccountNotFoundException("No account found for " + auth.getName());
		}
		
		return new TokenResponse(usr, token);
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	class TokenResponse {
		Person user;
		String accessToken;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if(personService.getUser(DEFAULT_ADMIN) == null) {
			createDefaultUsers();
		}
	}
	
	private void createDefaultUsers() {
		Person admin = new Person();
		admin.setEmail(DEFAULT_ADMIN);
		admin.setPassword(DEFAULT_ADMIN);
		admin.setFirstName("Show");
		admin.setLastName("Admin");
		admin.addRole(UserRoles.ROLE_ADMIN);
		
		try {
			register(admin);
		}
		catch(URISyntaxException e) {
			log.error("Error while creating admin user", e);
		}
	}

}
