package org.eihq.quiltshow.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class Category {

	@Id
	@GeneratedValue
	private Long id;

	private String name;

	@Column(length=50)
	private String shortDescription;

	@Column(length=5000)
	private String description;
	
	int displayOrder;

	@JsonIgnore
	@ManyToOne
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private Show show;
	
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = false, mappedBy = "category")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	Set<Quilt> quilts = new HashSet<>();
	
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = false, mappedBy = "category")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	Set<Award> awards = new HashSet<>();
	
	private Boolean judgeable = Boolean.TRUE;
	


	public Category(Long id) {
		this.id = id;
	}

	
	public String getShortDescription() {
		if(!StringUtils.hasText(this.shortDescription)) {
			return description.length() > 50 ? description.substring(0, 50) + "..." : description;
		}
		
		return this.shortDescription;
	}

}
