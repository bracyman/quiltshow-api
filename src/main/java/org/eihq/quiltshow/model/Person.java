package org.eihq.quiltshow.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "people")
@Data
@NoArgsConstructor
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

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "enteredBy")
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
}
