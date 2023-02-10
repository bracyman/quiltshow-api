package org.eihq.quiltshow.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "tag_categories")
@Data
@NoArgsConstructor
@JsonIdentityInfo(
		  generator = ObjectIdGenerators.PropertyGenerator.class, 
		  property = "id")
public class TagCategory {

	@Id
	@GeneratedValue
	Long id;

	String name;

	String description;
	
	Boolean onlyOne = Boolean.FALSE;
	
	Boolean requireOne = Boolean.FALSE;

	@JsonIgnore
	@ManyToOne
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	Show show;

	@JsonManagedReference
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "tagCategory")
	@EqualsAndHashCode.Exclude
	Set<Tag> tags = new HashSet<>();
}
