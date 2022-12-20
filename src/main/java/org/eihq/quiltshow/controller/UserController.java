package org.eihq.quiltshow.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.eihq.quiltshow.model.Person;
import org.eihq.quiltshow.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private PersonService personService;

    @GetMapping
    public List<Person> getPersons() {
        return personService.getPersons();
    }

    @PostMapping
    public ResponseEntity<Person> createPerson(@RequestBody Person Person) throws URISyntaxException {
        Person newPerson = personService.save(Person);
        return ResponseEntity.created(new URI(String.format("/Persons/%d", newPerson.getId()))).body(newPerson);
    }

    @GetMapping("/current")
    public Person getPerson(Authentication auth) {
		String username = auth.getName();
		
		return personService.getUser(username);
    }

    @GetMapping("/{userId}")
    public Person getPerson(Authentication auth, @PathVariable Long userId) {
    	if(userId == null) {
    		Object obj = auth.getCredentials();
    		return null;
    	}
    	else {
    		return personService.getPerson(userId);
    	}
    }
    
    @PutMapping("/{userId}")
    public ResponseEntity<Person> updatePerson(@PathVariable Long userId, @RequestBody Person updatedPerson) {
        Person person = personService.getPerson(userId);
        
        if(person == null) {
        	return ResponseEntity.notFound().build();
        }
        
        if(updatedPerson.getFirstName() != null) person.setFirstName(updatedPerson.getFirstName());
        if(updatedPerson.getLastName() != null) person.setLastName(updatedPerson.getLastName());
        if(updatedPerson.getEmail() != null) person.setEmail(updatedPerson.getEmail());
        
        person = personService.save(person);

        return ResponseEntity.ok(person);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePerson(@PathVariable Long id) {
        personService.delete(id);
        return ResponseEntity.ok().build();
    }
}
