package org.eihq.quiltshow.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "tags")
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class Tag {

	@Id
	@GeneratedValue
	Long id;

	String name;

	String description;

	
	@JsonBackReference
	@ManyToOne
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	TagCategory tagCategory;

	@JsonIgnore
	@ManyToMany(mappedBy = "tags", fetch = FetchType.EAGER)
	@EqualsAndHashCode.Exclude
	Set<Quilt> quilts = new HashSet<>();
}
