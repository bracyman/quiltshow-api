package org.eihq.quiltshow.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.websocket.server.PathParam;

import org.eihq.quiltshow.configuration.UserRoles;
import org.eihq.quiltshow.model.HangingUnit;
import org.eihq.quiltshow.model.Room;
import org.eihq.quiltshow.repository.HangingUnitRepository;
import org.eihq.quiltshow.repository.RoomRepository;
import org.eihq.quiltshow.repository.WallRepository;
import org.eihq.quiltshow.service.UserAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
	RoomRepository roomRepository;
	
	@Autowired
	HangingUnitRepository hangingUnitRepository;
	
	@Autowired
	WallRepository wallRepository;
	
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
		
		return ResponseEntity.ok().body(roomRepository.findAll());
	}
	
	@PostMapping("")
	public ResponseEntity<Room> createRoom(Authentication auth, @RequestBody Room room) {
		if(!userAuthentication.hasRole(UserRoles.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		if(room == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		
		if(room.getId() != null) {
			return updateRoom(auth, room.getId(), room);
		}
		
		Room savedRoom = roomRepository.save(room);
		
		return ResponseEntity.ok().body(savedRoom);
	}
	
	
	@GetMapping("/{id}")
	public ResponseEntity<Room> getRoom(Authentication auth, @PathParam("id") Long id) {
		if(!userAuthentication.hasRole(UserRoles.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		Optional<Room> room = roomRepository.findById(id);
		
		if(room.isPresent()) {
			return ResponseEntity.ok().body(room.get());
		}
		else {
			return ResponseEntity.notFound().build();
		}
	}
	
	
	@PutMapping("/{id}")
	public ResponseEntity<Room> updateRoom(Authentication auth, @PathParam("id") Long id, @RequestBody Room room) {
		if(!userAuthentication.hasRole(UserRoles.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		if((room == null) || !room.getId().equals(id)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		
		Room updatedRoom = roomRepository.save(room);
		return ResponseEntity.ok().body(updatedRoom);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteRoom(Authentication auth, @PathParam("id") Long id) {
		if(!userAuthentication.hasRole(UserRoles.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		roomRepository.deleteById(id);
		return ResponseEntity.ok(null);
	}
	
	
	
	/* ********************************************************************* */
	/*   Hanging Unit functions                                              */
	/* ********************************************************************* */
	@GetMapping("/{roomId}/hanging-units")
	public ResponseEntity<List<HangingUnit>> getRoomHangingUnits(Authentication auth, @PathParam("roomId") Long roomId) {
		if(!userAuthentication.hasRole(UserRoles.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		Optional<Room> room = roomRepository.findById(roomId);
		
		if(room.isPresent()) {
			return ResponseEntity.ok().body(room.get().getHangingUnits());
		}
		else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@PostMapping("/{roomId}/hanging-units")
	public ResponseEntity<HangingUnit> addHangingUnit(Authentication auth, @PathParam("roomId") Long roomId, @RequestBody HangingUnit hangingUnit) {
		if(!userAuthentication.hasRole(UserRoles.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		if(hangingUnit == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		
		if(hangingUnit.getId() != null) {
			return updateHangingUnit(auth, roomId, hangingUnit.getId(), hangingUnit);
		}
		
		Optional<Room> room = roomRepository.findById(roomId);
		
		if(!room.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		
		room.get().getHangingUnits().add(hangingUnit);
		roomRepository.save(room.get());
		
		return ResponseEntity.ok().body(hangingUnit);
	}

	@GetMapping("/{roomId}/hanging-units/{unitId}")
	public ResponseEntity<HangingUnit> getHangingUnit(Authentication auth, @PathParam("roomId") Long roomId, @PathParam("unitId") Long unitId) {
		if(!userAuthentication.hasRole(UserRoles.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		if(unitId == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		
		Optional<Room> room = roomRepository.findById(roomId);
		
		if(!room.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		
		Optional<HangingUnit> hangingUnit = room.get().getHangingUnits().stream().filter(h -> h.getId().equals(unitId)).findFirst();
		
		return hangingUnit.isPresent()
				? ResponseEntity.ok().body(hangingUnit.get())
				: ResponseEntity.notFound().build();
	}

	@PutMapping("/{roomId}/hanging-units/{unitId}")
	public ResponseEntity<HangingUnit> updateHangingUnit(Authentication auth, @PathParam("roomId") Long roomId, @PathParam("unitId") Long unitId, HangingUnit hangingUnit) {
		if(!userAuthentication.hasRole(UserRoles.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		if((hangingUnit == null) || (hangingUnit.getId() == null) || !hangingUnit.getId().equals(unitId)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		
		Optional<Room> room = roomRepository.findById(roomId);
		
		if(!room.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		
		hangingUnitRepository.save(hangingUnit);
		
		return ResponseEntity.ok().body(hangingUnit);
	}
}
