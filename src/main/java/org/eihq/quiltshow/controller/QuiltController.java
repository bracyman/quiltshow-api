package org.eihq.quiltshow.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eihq.quiltshow.model.Person;
import org.eihq.quiltshow.model.Quilt;
import org.eihq.quiltshow.repository.PersonRepository;
import org.eihq.quiltshow.repository.QuiltRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("/quilts")
public class QuiltController {

	@Autowired
	private QuiltRepository quiltRepository;

	@Autowired
	private PersonRepository personRepository;

	@GetMapping
	public List<Quilt> getQuilts() {
		return quiltRepository.findAll();
	}

	@GetMapping("/user/{userId}")
	public Map<String, List<Quilt>> getQuiltsForUser(@PathVariable Long userId) {
		Optional<Person> p = personRepository.findById(userId);
		
		if(p.isPresent()) {
			Map<String, List<Quilt>> quilts = new HashMap<>();
			quilts.put(Person.PIECER, p.get().getPieced());
			quilts.put(Person.QUILTER, p.get().getQuilted());
		}
		
		return Collections.emptyMap();
	}

	@GetMapping("/{id}")
	public Quilt getQuilt(@PathVariable Long id) {
		return quiltRepository.findById(id).get();
	}

	@PostMapping
	public ResponseEntity<Quilt> createQuilt(@RequestBody Quilt quilt) throws URISyntaxException {
		Quilt newQuilt = quiltRepository.save(quilt);
		return ResponseEntity.created(new URI(String.format("/quilts/%d", newQuilt.getId()))).body(newQuilt);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Quilt> updateQuilt(@PathVariable Long id, @RequestBody Quilt quilt) {
		Quilt currentQuilt = quiltRepository.findById(id).orElseThrow(RuntimeException::new);
		currentQuilt.setName(quilt.getName());
		currentQuilt.setDescription(quilt.getDescription());
		currentQuilt.setLength(quilt.getLength());
		currentQuilt.setWidth(quilt.getWidth());
		currentQuilt.setPiecedBy(quilt.getPiecedBy());
		currentQuilt.setQuiltedBy(quilt.getQuiltedBy());
		currentQuilt.setTags(quilt.getTags());

		currentQuilt = quiltRepository.save(currentQuilt);

		return ResponseEntity.ok(currentQuilt);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteQuilt(@PathVariable Long id) {
		quiltRepository.deleteById(id);
		return ResponseEntity.ok().build();
	}
}
