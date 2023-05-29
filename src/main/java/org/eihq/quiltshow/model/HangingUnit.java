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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "hanging_units")
@EqualsAndHashCode
@JsonIdentityInfo(
		  generator = ObjectIdGenerators.PropertyGenerator.class, 
		  property = "id")
public class HangingUnit {
	public static final double DEFAULT_HEIGHT = 10.0;
	
	
	public static enum Types {
		WALL,
		UBOOTH,
		HBOOTH,
		BLOCK,
		DOOR
	};

	
	@Id
    @GeneratedValue
    Long id;

	Types unitType;
		
	String name;
	
	@Convert(converter = MeasurementMapConverter.class)
	Map<String, Double> location;
	
	@Convert(converter = MeasurementMapConverter.class)
	Map<String, Double> size;

	@OneToMany(mappedBy = "hangingUnit", cascade = CascadeType.ALL)
	@EqualsAndHashCode.Exclude
	List<Wall> walls;
	
	@ManyToOne
	@EqualsAndHashCode.Exclude
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
		case WALL:
			getWalls().add(wall("A", getSize().get("length"), getSize().get("height")));
			getWalls().add(wall("B", getSize().get("length"), getSize().get("height")));
			break;
			
		case UBOOTH:
			getWalls().add(wall("A", getSize().get("depth"), getSize().get("height")));
			getWalls().add(wall("B", getSize().get("width"), getSize().get("height")));
			getWalls().add(wall("C", getSize().get("depth"), getSize().get("height")));
			getWalls().add(wall("OA", getSize().get("depth"), getSize().get("height")));
			getWalls().add(wall("OB", getSize().get("width"), getSize().get("height")));
			getWalls().add(wall("OC", getSize().get("depth"), getSize().get("height")));
			break;
			
		case HBOOTH:
			getWalls().add(wall("A", getSize().get("upperDepth"), getSize().get("height")));
			getWalls().add(wall("B", getSize().get("width"), getSize().get("height")));
			getWalls().add(wall("C", getSize().get("upperDepth"), getSize().get("height")));
			getWalls().add(wall("D", getSize().get("lowerDepth"), getSize().get("height")));
			getWalls().add(wall("E", getSize().get("width"), getSize().get("height")));
			getWalls().add(wall("F", getSize().get("lowerDepth"), getSize().get("height")));
			getWalls().add(wall("OA", getSize().get("upperDepth") + getSize().get("lowerDepth"), getSize().get("height")));
			getWalls().add(wall("OC", getSize().get("upperDepth") + getSize().get("lowerDepth"), getSize().get("height")));
			break;
			
		case BLOCK:
		case DOOR:			
		default:
			// these units have no hangable surfaces
		}
	}
	
	private Wall wall(String name, Double width, Double height) {
		Wall wall = new Wall();
		wall.setWidth(width);
		wall.setHeight(height == null ? DEFAULT_HEIGHT : height);
		wall.setName(name);
		wall.setHangingUnit(this);
		
		return wall;
	}
}
