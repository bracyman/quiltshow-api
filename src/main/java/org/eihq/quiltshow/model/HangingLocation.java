package org.eihq.quiltshow.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity()
@Table(name = "hangingLocations")
public class HangingLocation {
	
	 
		@Id
	    @GeneratedValue
	    Long id;

		@JsonIgnore
	    @ManyToOne
	    @EqualsAndHashCode.Exclude 
		@ToString.Exclude
		Show show;
		
	    String name;
	    
	    @OneToMany(mappedBy = "hangingLocation", fetch = FetchType.EAGER)
	    List<Quilt> quilts;
}
