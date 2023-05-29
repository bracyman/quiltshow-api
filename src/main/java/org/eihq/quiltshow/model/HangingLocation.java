package org.eihq.quiltshow.model;

import java.util.Map;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.eihq.quiltshow.repository.MeasurementMapConverter;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "hanging_locations")
@EqualsAndHashCode
@JsonIdentityInfo(
		  generator = ObjectIdGenerators.PropertyGenerator.class, 
		  property = "id")
public class HangingLocation {
	
	@Id
    @GeneratedValue
    Long id;

	@Convert(converter = MeasurementMapConverter.class)
	Map<String, Double> location;
	
	@ManyToOne
	@EqualsAndHashCode.Exclude
	Wall wall;
	
	@OneToOne
	@EqualsAndHashCode.Exclude
	Quilt quilt;
	
	@Transient
	public Double getHeight() {
		return (getQuilt() == null) ? 0.0 : getQuilt().getLength();
	}
	
	@Transient
	public Double getWidth() {
		return (getQuilt() == null) ? 0.0 : getQuilt().getWidth();
	}
	
}
