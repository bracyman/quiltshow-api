package org.eihq.quiltshow.repository;

import java.util.List;

import org.eihq.quiltshow.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

	public List<Person> findByEmailIgnoreCase(String email);

	public List<Person> findByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);
}
