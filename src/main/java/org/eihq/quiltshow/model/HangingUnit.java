package org.eihq.quiltshow.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.eihq.quiltshow.repository.MeasurementMapConverter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "hanging_units")
public class HangingUnit {
	public static final double DEFAULT_HEIGHT = 10.0;
	
	
	public static enum Types {
		SINGLE_SIDE_WALL,
		DOUBLE_SIDE_WALL,
		SINGLE_BOOTH,
		DOUBLE_BOOTH
	};

	
	@Id
    @GeneratedValue
    Long id;

	Types unitType;
	
	double leftPosition;
	
	double topPosition;
	
	double height = DEFAULT_HEIGHT;
	
	double angle;
	
	String name;
	
	@OneToMany(mappedBy = "hangingUnit", cascade = CascadeType.ALL)
	List<Wall> walls;
	
	@Convert(converter = MeasurementMapConverter.class)
	Map<String, Double> measurements;
	
	@ManyToOne
	Room room;
	
	
	@JsonIgnore
	@Transient
	public List<Quilt> getQuilts() {
		List<Quilt> quilts = new LinkedList<>();
		walls.forEach(w -> quilts.addAll(w.getQuilts()));
		
		return quilts;
	}
	
	
	public void createWalls() {
		setWalls(new LinkedList<>());
		
		switch(getUnitType()) {
		case SINGLE_SIDE_WALL:
			
			break;
			
		case DOUBLE_SIDE_WALL:
			break;
			
		case SINGLE_BOOTH:
			break;
			
		case DOUBLE_BOOTH:
			break;
			
		default:
			break;
		}
	}
	
	@Transient
	public Double getWidth() {
		switch(getUnitType()) {
		case SINGLE_SIDE_WALL:
			
			return 0.0;
			
		case DOUBLE_SIDE_WALL:
			return 0.0;
			
		case SINGLE_BOOTH:
			return 0.0;
			
		case DOUBLE_BOOTH:
			return 0.0;
			
		default:
			return 0.0;
		}
	}
	
	
	@Transient
	public Double getHeight() {
		switch(getUnitType()) {
		case SINGLE_SIDE_WALL:
			return 0.0;
			
		case DOUBLE_SIDE_WALL:
			return 0.0;
			
		case SINGLE_BOOTH:
			return 0.0;
			
		case DOUBLE_BOOTH:
			return 0.0;
			
		default:
			return 0.0;
		}
	}
}
