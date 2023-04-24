package org.eihq.quiltshow.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "hanging_locations")
public class HangingLocation {
	
	@Id
    @GeneratedValue
    Long id;

	Double leftPosition = 0.0;
	
	Double topPosition = 0.0;
	
	@ManyToOne
	Wall wall;
	
	@OneToOne
	Quilt quilt;
	
	@Transient
	public Double getHeight() {
		return (getQuilt() == null) ? 0.0 : getQuilt().getLength();
	}
	
	@Transient
	public Double getWidth() {
		return (getQuilt() == null) ? 0.0 : getQuilt().getWidth();
	}
	
	@Transient
	public String getName() {
		return wall.getName();
	}
}
