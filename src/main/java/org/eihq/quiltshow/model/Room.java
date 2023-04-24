package org.eihq.quiltshow.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "rooms")
public class Room {
	
	@Id
    @GeneratedValue
    Long id;

	String name;
	
	@ManyToOne
	Show show;
	
	Boolean active = Boolean.FALSE;
	
	Double width;
	
	Double length;
	
	Double maxHeight;
	
	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	List<HangingUnit> hangingUnits;
}
