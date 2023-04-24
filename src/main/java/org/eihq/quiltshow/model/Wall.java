package org.eihq.quiltshow.model;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "walls")
public class Wall {

	@Id
    @GeneratedValue
    Long id;

	String name;
	
	Double width;
	
	Double height;
	
	@OneToMany(mappedBy = "wall", cascade = CascadeType.ALL)
	List<HangingLocation> hangingLocations;
	
	@ManyToOne
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
		hangingLocation.setLeftPosition(leftPosition);
		hangingLocation.setTopPosition(topPosition);
		hangingLocation.setQuilt(quilt);
		
		getHangingLocations().add(hangingLocation);
		return hangingLocation;
	}
	
	public HangingLocation removeQuilt(Quilt quilt) {
		HangingLocation hangingLocation = hangingLocations.stream().filter(hl -> quilt.getId().equals(hl.getQuilt().getId())).findFirst().orElse(null);
		
		if(hangingLocation != null) {
			hangingLocation.getQuilt().setHangingLocation(null);
			hangingLocation.setQuilt(null);
			getHangingLocations().remove(hangingLocation);
		}
		
		return hangingLocation;
	}
	
	public void clearWall() {
		hangingLocations.forEach(hl -> {
			hl.getQuilt().setHangingLocation(null);
		});
		
		hangingLocations.clear();
	}
}
