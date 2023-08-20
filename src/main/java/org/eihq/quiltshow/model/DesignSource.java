package org.eihq.quiltshow.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "design_sources")
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class DesignSource {


	@Id
    @GeneratedValue
    private Long id;

	private DesignSourceType designSourceType;
	
	private String name;
	
	private String title;
	
	private String publishedYear;
	
	private String author;
	
	private String issueNumber;
	
	private String contactInfo;
	
	@JsonIgnore
	@OneToOne(mappedBy = "designSource")
    @EqualsAndHashCode.Exclude
	private Quilt quilt;
}
