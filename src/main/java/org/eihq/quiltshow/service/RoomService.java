package org.eihq.quiltshow.service;

import java.util.List;
import java.util.Optional;

import org.eihq.quiltshow.exception.NotFoundException;
import org.eihq.quiltshow.model.HangingLocation;
import org.eihq.quiltshow.model.HangingUnit;
import org.eihq.quiltshow.model.Quilt;
import org.eihq.quiltshow.model.Room;
import org.eihq.quiltshow.model.Wall;
import org.eihq.quiltshow.repository.HangingLocationRepository;
import org.eihq.quiltshow.repository.HangingUnitRepository;
import org.eihq.quiltshow.repository.QuiltRepository;
import org.eihq.quiltshow.repository.RoomRepository;
import org.eihq.quiltshow.repository.WallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RoomService {

	@Autowired
	RoomRepository roomRepository;
	
	@Autowired
	QuiltRepository quiltRepository;
	
	@Autowired
	HangingUnitRepository hangingUnitRepository;
	
	@Autowired
	HangingLocationRepository hangingLocationRepository;
	
	@Autowired
	WallRepository wallRepository;


	/* ********************************************************************* */
	//  Room operations
	/* ********************************************************************* */	
	public List<Room> getRooms() {
		return roomRepository.findAll();
	}
	
	public Optional<Room> getRoom(Long id) {
		return roomRepository.findById(id);
	}
	
	public Room saveRoom(Room room) {
		room.getHangingUnits().forEach(hangingUnit -> {
			if(hangingUnit.getId() == null) {
				hangingUnit.createWalls();
			}
			hangingUnit.setRoom(room);
		});
		
		Room updatedRoom = roomRepository.saveAndFlush(room);
		log.info("Saved room [{}]", updatedRoom.getId());
		
		updatedRoom.getHangingUnits().forEach(unit -> {
			saveHangingUnit(updatedRoom, unit);
		});
		
		return updatedRoom;
	}
	
	public void deleteRoom(Long id) {
		Room room = roomRepository.findById(id).orElse(null);
		
		if(room != null) {
			deleteRoom(room);
		}
	}
	
	public void deleteRoom(Room room) {
		if(room == null) {
			return;
		}
		
		// disconnect any quilts from hanging locations
		clearQuilts(room);
		roomRepository.delete(room);
	}
	
	public void clearQuilts(Room room) {
		if(room == null) { return; }
		if(room.getHangingUnits() == null) { return; }

		room.getHangingUnits().forEach(hangingUnit -> { clearQuilts(hangingUnit); });
	}


	/* ********************************************************************* */
	//  Hanging Unit operations
	/* ********************************************************************* */
	public Optional<HangingUnit> getHangingUnit(Long id) {
		return hangingUnitRepository.findById(id);
	}
	/**
	 * If creating a new hanging unit, also creates the walls for that hanging unit
	 * @param hangingUnit
	 * @return
	 */
	public HangingUnit saveHangingUnit(Room room, HangingUnit hangingUnit) {
		if(hangingUnit.getId() == null) {
			hangingUnit.createWalls();
			hangingUnit.setRoom(room);
			HangingUnit updatedHangingUnit = hangingUnitRepository.saveAndFlush(hangingUnit);
			
			room.getHangingUnits().add(updatedHangingUnit);
			roomRepository.saveAndFlush(room);
			
			log.info("Created new {} unit [{}]", updatedHangingUnit.getUnitType(), updatedHangingUnit.getId());
			return updatedHangingUnit;
		}
		else {
			HangingUnit updatedUnit = hangingUnitRepository.saveAndFlush(hangingUnit);
			
			updatedUnit.getWalls().forEach(wall -> {
				saveWall(wall);
			});
			
			return updatedUnit;
		}
	}
	
	
	public void deleteHangingUnit(Long id) {
		HangingUnit hangingUnit = hangingUnitRepository.findById(id).orElse(null);
		if(hangingUnit != null) {
			deleteHangingUnit(hangingUnit);
		}
	}
	
	public void deleteHangingUnit(HangingUnit hangingUnit) {
		if(hangingUnit == null) { return; }

		clearQuilts(hangingUnit);
		hangingUnit.getRoom().getHangingUnits().remove(hangingUnit);
		roomRepository.saveAndFlush(hangingUnit.getRoom());
		hangingUnitRepository.delete(hangingUnit);
	}
		
	public void clearQuilts(HangingUnit hangingUnit) {
		if(hangingUnit == null) { return; }
		if(hangingUnit.getWalls() == null) { return; }
		
		hangingUnit.getWalls().forEach(wall -> { clearQuilts(wall); });
	}
	


	/* ********************************************************************* */
	//  Wall operations
	/* ********************************************************************* */
	public Wall saveWall(Wall wall) {
		Wall updatedWall = wallRepository.saveAndFlush(wall);
		
		if(updatedWall.getHangingLocations() != null) {
			updatedWall.getHangingLocations().forEach(location -> {
				location.setWall(updatedWall);
				hangingLocationRepository.saveAndFlush(location);
			});
		}
		
		return updatedWall;
	}
	
	public Optional<HangingLocation> getHangingLocation(Long hangingLocationId) {
		return hangingLocationRepository.findById(hangingLocationId);
	}

	public HangingLocation hangQuilt(Long wallId, HangingLocation hangingLocation) {
		Wall wall = wallRepository.findById(wallId)
				.orElseThrow(() -> new NotFoundException(String.format("unable to find wall %d", wallId)) );
		Quilt quilt = quiltRepository.findById(hangingLocation.getQuilt().getId())
				.orElseThrow(() -> new NotFoundException(String.format("unable to find quilt %d", hangingLocation.getQuilt().getId())) );

		wall.getHangingLocations().add(hangingLocation);
		hangingLocation.setQuilt(quilt);
		hangingLocation.setWall(wall);
		
		HangingLocation updatedLocation = hangingLocationRepository.saveAndFlush(hangingLocation);
		
		log.info("Hung quilt {} on wall {}", quilt.getNumber(), wall.getName());
		
		return updatedLocation;
	}
	

	public HangingLocation saveHangingLocation(Long hangingLocationId, HangingLocation hangingLocation) {
		HangingLocation location = hangingLocationRepository.findById(hangingLocationId)
				.orElseThrow(() -> new NotFoundException(String.format("unable to find hanging location %d", hangingLocationId)) );
		
		location.setLocation(hangingLocation.getLocation());
		return hangingLocationRepository.save(location);
	}
	
	public Optional<HangingLocation> getQuiltLocation(Long quiltId) {
		List<HangingLocation> locations = hangingLocationRepository.findAll();
		Optional<HangingLocation> location = locations.stream().filter(l -> l.getQuilt().getId().equals(quiltId)).findFirst();
		
		return location;
	}

	public void unhangQuilt(Long quiltId) {
		Quilt quilt = quiltRepository.findById(quiltId).orElseThrow(() -> new NotFoundException(String.format("unable to find quilt %d", quiltId)) );
		
		Optional<HangingLocation> location = getQuiltLocation(quilt.getId());
		if(location.isPresent()) {
			unhangQuilt(location.get());
		}
	}
	
	public void unhangQuilt(HangingLocation hangingLocation) {
		hangingLocation.getWall().getHangingLocations().remove(hangingLocation);
		wallRepository.save(hangingLocation.getWall());
		
		hangingLocationRepository.delete(hangingLocation);
	}

	public void clearQuilts(Wall wall) {
		if(wall == null) { return; }
		if(wall.getHangingLocations() == null) { return; }
		
		wall.getHangingLocations().forEach(location -> {
			location.setQuilt(null);
		});
		
	}

}
