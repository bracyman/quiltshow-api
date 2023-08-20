package org.eihq.quiltshow.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "walls")
@EqualsAndHashCode
@JsonIdentityInfo(
		  generator = ObjectIdGenerators.PropertyGenerator.class, 
		  property = "id")
public class Wall {

	@Id
    @GeneratedValue
    Long id;

	String name;
	
	Double width;
	
	Double height;
	
	@OneToMany(mappedBy = "wall", cascade = CascadeType.ALL)
	@EqualsAndHashCode.Exclude
	List<HangingLocation> hangingLocations;
	
	@ManyToOne
	@EqualsAndHashCode.Exclude
	HangingUnit hangingUnit;
	
	

	@JsonIgnore
	@Transient
	public List<Quilt> getQuilts() {
		if(hangingLocations == null) {
			return Collections.emptyList();
		}
		
		return hangingLocations.stream().map(hl -> hl.getQuilt()).collect(Collectors.toList());
	}
	
	public HangingLocation addQuilt(Quilt quilt, Double leftPosition, Double topPosition) {
		HangingLocation hangingLocation = new HangingLocation();
		
		Map<String, Double> location = new HashMap<>();
		location.put("left", leftPosition);
		location.put("top", topPosition);
		hangingLocation.setLocation(location);
		hangingLocation.setQuilt(quilt);
		hangingLocation.setWall(this);
		
		getHangingLocations().add(hangingLocation);
		return hangingLocation;
	}
	
	public HangingLocation removeQuilt(Quilt quilt) {
		HangingLocation hangingLocation = hangingLocations.stream().filter(hl -> quilt.getId().equals(hl.getQuilt().getId())).findFirst().orElse(null);
		
		if(hangingLocation != null) {
			hangingLocation.setQuilt(null);
			getHangingLocations().remove(hangingLocation);
		}
		
		return hangingLocation;
	}
	
	public void clearWall() {
		hangingLocations.clear();
	}
}
