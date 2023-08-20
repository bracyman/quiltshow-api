package org.eihq.quiltshow.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.eihq.quiltshow.configuration.UserRoles;
import org.eihq.quiltshow.repository.UserRoleListConverter;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "people")
@Data
@EqualsAndHashCode
public class Person {

	@Id
	@GeneratedValue
	private Long id;

	private String email;

	@JsonIgnore
	private String password;
	
	private LocalDateTime createOn = LocalDateTime.now();
	private LocalDateTime lastLoggedIn;

	private String firstName;
	private String lastName;
	
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String zip;
	private String phone;
	
	@Convert(converter = UserRoleListConverter.class)
	private List<UserRoles> roles = new LinkedList<>();
	
	
	/**
	 * Defaults to a standard user
	 */
	public Person() {
		this.roles.add(UserRoles.ROLE_USER);
	}
	
	

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "enteredBy", fetch = FetchType.EAGER)
	private List<Quilt> entered = new ArrayList<>();

	public String getFullName() {
		return String.format("%s %s", getFirstName(), getLastName());
	}
	
	public Person addQuilt(Quilt quilt) {
		getEntered().add(quilt);
		quilt.setEnteredBy(this);
		return this;
	}
	
	public Person removeQuilt(Quilt quilt) {
		getEntered().remove(quilt);
		quilt.setEnteredBy(null);
		return this;
	}
	
	public Person addRole(UserRoles role) {
		if(!this.roles.contains(role)) {
			this.roles.add(role);
		}
		
		return this;
	}
	
	public Person removeRole(UserRoles role) {
		if(this.roles.contains(role)) {
			this.roles.remove(role);
		}
		
		return this;
	}
}
