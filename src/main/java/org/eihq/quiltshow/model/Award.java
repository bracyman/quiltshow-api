package org.eihq.quiltshow.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "awards")
@Data
@EqualsAndHashCode
public class Award {
	

	@Id
	@GeneratedValue
	private Long id;

	private String name;
	
	private String color;
	
	private String description;
	
	private Integer displayOrder;
	
	private Boolean multipleRecipients = Boolean.FALSE;
	
	@JsonIgnore
	@ManyToOne
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private Show show;

	@JsonIgnore
	@ManyToMany(mappedBy = "awards")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private Set<Quilt> awardedTo;
	
	
	public Award() {
		awardedTo = new HashSet<>();
	}
	
	@ManyToOne
	private Category category;
}
