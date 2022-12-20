package org.eihq.quiltshow.service;

import java.util.List;
import java.util.Optional;

import org.eihq.quiltshow.model.Person;
import org.eihq.quiltshow.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

	@Autowired
	PersonRepository personRepository;
	
	
	public List<Person> getpersons() {
		return personRepository.findByIsUser(true);
	}
	
	public List<Person> getPersons() {
		return personRepository.findAll();
	}
	
	public List<Person> getNonUserPersons() {
		return personRepository.findByIsUser(false);
	}
	
	public Person getPerson(Long id) {
		Optional<Person> person = personRepository.findById(id);
		
		if(person.isPresent()) {
			return person.get();
		}
		
		return null;
	}
	
	public Person getUser(String email) {
		List<Person> persons = personRepository.findByEmail(email);
		
		if(!persons.isEmpty()) {
			return persons.get(0);
		}
		
		return null;
	}
	
	public Person save(Person person) {
		Person saved = personRepository.save(person);
		return saved;
	}
	
	public void delete(String email) {
		Person user = getUser(email);
		personRepository.delete(user);
	}
	
	public void delete(Person person) {
		personRepository.delete(person);
	}
	
	public void delete(Long id) {
		personRepository.deleteById(id);;
	}
}
