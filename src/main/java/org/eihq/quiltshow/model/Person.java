package org.eihq.quiltshow.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "persons")
@Data
@NoArgsConstructor
public class Person {
	
	public static final String PIECER 	= "piecer";
	public static final String QUILTER 	= "quilter";

	@Id
	@GeneratedValue
	private Long id;

	private String email;
	private String firstName;
	private String lastName;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "piecedBy")
	private List<Quilt> pieced;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "quiltedBy")
	private List<Quilt> quilted;

	public String getFullName() {
		return String.format("%s %s", getFirstName(), getLastName());
	}
}
