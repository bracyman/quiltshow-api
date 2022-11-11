package org.eihq.quiltshow.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.eihq.quiltshow.model.Person;
import org.eihq.quiltshow.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/persons")
public class PersonController {
    @Autowired
    private PersonRepository personRepository;

    @GetMapping
    public List<Person> getPersons() {
        return personRepository.findAll();
    }

    @GetMapping("/{id}")
    public Person getPerson(@PathVariable Long id) {
        return personRepository.findById(id).get();
    }

    @PostMapping
    public ResponseEntity<Person> createPerson(@RequestBody Person person) throws URISyntaxException {
        Person newPerson = personRepository.save(person);
        return ResponseEntity.created(new URI("/persons/%d".formatted(newPerson.getId()))).body(newPerson);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Person> updateClient(@PathVariable Long id, @RequestBody Person person) {
        Person currentPerson = personRepository.findById(id).orElseThrow(RuntimeException::new);
        currentPerson.setFirstName(person.getFirstName());
        currentPerson.setLastName(person.getLastName());
        currentPerson.setPieced(person.getPieced());
        currentPerson.setQuilted(person.getQuilted());
        
        currentPerson = personRepository.save(currentPerson);

        return ResponseEntity.ok(currentPerson);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletPerson(@PathVariable Long id) {
        personRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
