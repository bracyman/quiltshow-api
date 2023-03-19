package org.eihq.quiltshow.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "shows")
@Data
public class Show {

	@Id
	@GeneratedValue
	private Long id;
	
	private String name;
	private String description;
	private String logo;
	
	private boolean active;
	
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String zipCode;
	
	private String mapLink;
	
	private Date startDate;
	private Date endDate;
	private Date entryStartDate;
	private Date entryEndDate;
	
	private String raffleQuiltName;
	private String raffleQuiltImage;
	private String raffleQuiltDescription;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "show", fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
    @EqualsAndHashCode.Exclude
	@ToString.Exclude
	private Set<Category> categories;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "show", fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
    @EqualsAndHashCode.Exclude
	@ToString.Exclude
	private Set<TagCategory> tagCategories;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "show", fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
    @EqualsAndHashCode.Exclude
	@ToString.Exclude
	private Set<Award> awards;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "show", fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
    @EqualsAndHashCode.Exclude
	@ToString.Exclude
	private Set<Quilt> quilts;
	
	public Show() {
		categories = new HashSet<>();
		tagCategories = new HashSet<>();
		awards = new HashSet<>();
		quilts = new HashSet<>();
	}	
}
