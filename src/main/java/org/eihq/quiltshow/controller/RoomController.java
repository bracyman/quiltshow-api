package org.eihq.quiltshow.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.eihq.quiltshow.configuration.UserRoles;
import org.eihq.quiltshow.exception.NotFoundException;
import org.eihq.quiltshow.model.HangingLocation;
import org.eihq.quiltshow.model.HangingUnit;
import org.eihq.quiltshow.model.Room;
import org.eihq.quiltshow.service.RoomService;
import org.eihq.quiltshow.service.UserAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("/api/rooms")
public class RoomController {


	@Autowired
	RoomService roomService;
	
	@Autowired
	UserAuthentication userAuthentication;
	
	
	/* ********************************************************************* */
	/*   Room functions                                              */
	/* ********************************************************************* */
	@GetMapping("")
	public ResponseEntity<List<Room>> getAllRooms(Authentication auth) {
		if(!userAuthentication.hasRole(UserRoles.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.emptyList());
		}
		
		return ResponseEntity.ok().body(roomService.getRooms());
	}
	
	@PostMapping("")
	public ResponseEntity<Room> createRoom(Authentication auth, @RequestBody Room room) {
		if(!userAuthentication.hasRole(UserRoles.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		if(room == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		
		Room savedRoom = roomService.saveRoom(room);
		
		return ResponseEntity.ok().body(savedRoom);
	}
	
	
	@GetMapping("/{id}")
	public ResponseEntity<Room> getRoom(Authentication auth, @PathVariable("id") Long id) {
		if(!userAuthentication.hasRole(UserRoles.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		Optional<Room> room = roomService.getRoom(id);
		
		return room.isPresent()
			? ResponseEntity.ok().body(room.get())
			: ResponseEntity.notFound().build();
	}
	
	
	@PutMapping("/{id}")
	public ResponseEntity<Room> updateRoom(Authentication auth, @PathVariable("id") Long id, @RequestBody Room room) {
		if(!userAuthentication.hasRole(UserRoles.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		if(room == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		
		room.setId(id);
		Room updatedRoom = roomService.saveRoom(room);
		return ResponseEntity.ok().body(updatedRoom);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteRoom(Authentication auth, @PathVariable("id") Long id) {
		if(!userAuthentication.hasRole(UserRoles.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		roomService.deleteRoom(id);
		return ResponseEntity.ok(null);
	}
	
	
	
	/* ********************************************************************* */
	/*   Hanging Unit functions                                              */
	/* ********************************************************************* */
	@GetMapping("/{roomId}/hanging-units")
	public ResponseEntity<List<HangingUnit>> getRoomHangingUnits(Authentication auth, @PathVariable("roomId") Long roomId) {
		if(!userAuthentication.hasRole(UserRoles.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		Optional<Room> room = roomService.getRoom(roomId);
		
		return room.isPresent()
			? ResponseEntity.ok().body(room.get().getHangingUnits())
			: ResponseEntity.notFound().build();
	}
	
	@PostMapping("/{roomId}/hanging-units")
	public ResponseEntity<HangingUnit> addHangingUnit(Authentication auth, @PathVariable("roomId") Long roomId, @RequestBody HangingUnit hangingUnit) {
		if(!userAuthentication.hasRole(UserRoles.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		if(hangingUnit == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		
		Optional<Room> room = roomService.getRoom(roomId);
		
		if(!room.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		HangingUnit updatedUnit = roomService.saveHangingUnit(room.get(), hangingUnit);
		
		return ResponseEntity.ok().body(updatedUnit);
	}

	@GetMapping("/{roomId}/hanging-units/{unitId}")
	public ResponseEntity<HangingUnit> getHangingUnit(Authentication auth, @PathVariable("roomId") Long roomId, @PathVariable("unitId") Long unitId) {
		if(!userAuthentication.hasRole(UserRoles.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		Optional<HangingUnit> hangingUnit = roomService.getHangingUnit(unitId);
		
		return hangingUnit.isPresent()
				? ResponseEntity.ok().body(hangingUnit.get())
				: ResponseEntity.notFound().build();
	}

	@PutMapping("/{roomId}/hanging-units/{unitId}")
	public ResponseEntity<HangingUnit> updateHangingUnit(Authentication auth, @PathVariable("roomId") Long roomId, @PathVariable("unitId") Long unitId, @RequestBody HangingUnit hangingUnit) {
		if(!userAuthentication.hasRole(UserRoles.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		if((hangingUnit == null) || (hangingUnit.getId() == null) || !hangingUnit.getId().equals(unitId)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		
		Optional<Room> room = roomService.getRoom(roomId);
		
		if(!room.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		
		roomService.saveHangingUnit(room.get(), hangingUnit);		
		return ResponseEntity.ok().body(hangingUnit);
	}
		
	@DeleteMapping("/{roomId}/hanging-units/{unitId}")
	public ResponseEntity<Void> deleteHangingUnit(Authentication auth, @PathVariable("roomId") Long roomId, @PathVariable("unitId") Long unitId) {
		if(!userAuthentication.hasRole(UserRoles.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		Optional<Room> room = roomService.getRoom(roomId);
		
		if(!room.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		
		roomService.deleteHangingUnit(unitId);	
		return ResponseEntity.ok(null);
	}

	
	@PostMapping("/walls/{wallId}")
	public ResponseEntity<HangingLocation> hangQuiltOnWall(
			Authentication auth, 
			@PathVariable("wallId") Long wallId,
			@RequestBody HangingLocation hangingLocation) {

		if(!userAuthentication.hasRole(UserRoles.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		HangingLocation updatedHangingLocation = roomService.hangQuilt(wallId, hangingLocation);
		
		return ResponseEntity.ok().body(updatedHangingLocation);
	}
	
	@PatchMapping("/quilts/{quiltId}")
	public ResponseEntity<HangingLocation> updateHangingLocation(Authentication auth, 
			@PathVariable("quiltId") Long quiltId, 
			@RequestBody HangingLocation hangingLocation) {
		
		HangingLocation updatedHangingLocation = roomService.saveHangingLocation(hangingLocation.getId(), hangingLocation);
		
		return ResponseEntity.ok().body(updatedHangingLocation);
	}
	
	@DeleteMapping("/{roomId}/hanging-units/{unitId}/walls/{wallId}/hanging-locations/{hangingLocationId}")
	public ResponseEntity<Void> unhangQuilt(
			Authentication auth, 
			@PathVariable("roomId") Long roomId, 
			@PathVariable("unitId") Long unitId, 
			@PathVariable("wallId") Long wallId,
			@PathVariable("hangingLocationId") Long hangingLocationId) {
		
		if(!userAuthentication.hasRole(UserRoles.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		Optional<Room> room = roomService.getRoom(roomId);		
		if(!room.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		Optional<HangingUnit> hangingUnit = roomService.getHangingUnit(unitId);		
		if(!hangingUnit.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		Optional<HangingLocation> hangingLocation = roomService.getHangingLocation(hangingLocationId);		
		if(!hangingLocation.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		try {
			roomService.unhangQuilt(hangingLocation.get());
			return ResponseEntity.ok(null); 
		}
		catch(NotFoundException e) {
			return ResponseEntity.notFound().build();
		}
		
	}
	
	@DeleteMapping("/quilts/{quiltId}")
	public ResponseEntity<Void> unhangQuilt(
			Authentication auth, @PathVariable("quiltId") Long quiltId) {
		
		if(!userAuthentication.hasRole(UserRoles.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		
		try {
			roomService.unhangQuilt(quiltId);
			return ResponseEntity.ok(null); 
		}
		catch(NotFoundException e) {
			return ResponseEntity.notFound().build();
		}
		
	}
	
}
