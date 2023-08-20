package org.eihq.quiltshow.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.data.annotation.Immutable;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Table(name = "quilt_search")
@EqualsAndHashCode
@Immutable
public class QuiltSearchData {

	@Id
	@Column(name = "quilt_id")
	Long id;
	
	@OneToOne()
	@JoinColumn(name = "quilt_id")
	Quilt quilt;
	
	@OneToOne()
	@JoinColumn(name = "hanging_location_id")
	HangingLocation hangingLocation;
	
	@ManyToOne()
	@JoinColumn(name = "wall_id")
	Wall wall;
	
	@ManyToOne()
	@JoinColumn(name = "hanging_unit_id")
	HangingUnit hangingUnit;
	

	public Long getId() {return id;}
	public Quilt getQuilt() {return quilt;}
	public HangingLocation getHangingLocation() {return hangingLocation;}
	public Wall getWall() {return wall;}
	public HangingUnit getHangingUnit() {return hangingUnit;}
	
	@Transient
	public void set(String field, Object value) {
		if(this.quilt == null) {
			this.quilt = new Quilt();
		}
		
		if(field.equals("id")) this.quilt.id = (Long)value;
		if(field.equals("number")) this.quilt.number = (Integer)value;
		if(field.equals("name")) this.quilt.name = (String)value;
		if(field.equals("description")) this.quilt.description = (String)value;
		if(field.equals("category")) this.quilt.category = (Category)value;
		if(field.equals("tags")) this.quilt.tags = (List<Tag>)value;
		if(field.equals("presidentsChallenge")) this.quilt.presidentsChallenge = (Boolean)value;
		if(field.equals("firstEntry")) this.quilt.firstEntry = (Boolean)value;
		if(field.equals("length")) this.quilt.length = (Double)value;
		if(field.equals("width")) this.quilt.width = (Double)value;
		if(field.equals("firstShow")) this.quilt.firstShow = (Boolean)value;
		if(field.equals("judged")) this.quilt.judged = (Boolean)value;
		if(field.equals("groupSize")) this.quilt.groupSize = (GroupSize)value;
		if(field.equals("mainColor")) this.quilt.mainColor = (String)value;
		if(field.equals("hangingPreference")) this.quilt.hangingPreference = (int)value;
		if(field.equals("designSource")) this.quilt.designSource = (DesignSource)value;
		if(field.equals("enteredBy")) this.quilt.enteredBy = (Person)value;
		if(field.equals("quiltedBy")) this.quilt.quiltedBy = (String)value;
		if(field.equals("additionalQuilters")) this.quilt.additionalQuilters = (String)value;
		if(field.equals("submittedOn")) this.quilt.submittedOn = (LocalDateTime)value;    
		if(field.equals("lastUpdatedOn")) this.quilt.lastUpdatedOn = (LocalDateTime)value;
		if(field.equals("awards")) this.quilt.awards = (Set<Award>)value;
	}
}
