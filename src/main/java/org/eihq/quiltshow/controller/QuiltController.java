package org.eihq.quiltshow.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import org.eihq.quiltshow.configuration.UserRoles;
import org.eihq.quiltshow.exception.PaymentException;
import org.eihq.quiltshow.model.PaymentData;
import org.eihq.quiltshow.model.Person;
import org.eihq.quiltshow.model.Quilt;
import org.eihq.quiltshow.repository.QuiltRepository;
import org.eihq.quiltshow.repository.QuiltSearchBuilder;
import org.eihq.quiltshow.service.PaymentService;
import org.eihq.quiltshow.service.PersonService;
import org.eihq.quiltshow.service.UserAuthentication;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("/quilts")
@Slf4j
public class QuiltController implements InitializingBean {
	
	private static final Integer STARTING_QUILT_NUMBER = 1000;

	@Autowired
	private QuiltRepository quiltRepository;

	@Autowired
	private PersonService personService;

	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private UserAuthentication userAuthentication;
	
	private int nextQuiltNumber = 1000;
	
	
	@Autowired
	QuiltSearchBuilder quiltSearchBuilder;

	@GetMapping("")
	public List<Quilt> getQuilts(Authentication auth, @RequestParam(name = "personal", required = false) String personal) {
		String email = auth.getName();
		boolean onlyPersonal = (personal != null) && "true".equalsIgnoreCase(personal);
		
		if(!onlyPersonal && userAuthentication.hasRole(UserRoles.ROLE_ADMIN)) {
			return quiltRepository.findAll();
		}
		
		Person user = personService.getUser(email);
		if(user != null) {
			return user.getEntered();
		}

		return Collections.emptyList();
	}
	

	@GetMapping("/all")
	public List<Quilt> getAllQuilts(Authentication auth) {
		return quiltRepository.findAll();
	}	

	@PostMapping("/search/{searchText}")
	public List<Quilt> searchQuilts(@RequestBody String searchText) {
		return quiltSearchBuilder.buildBasicSearch(searchText).getResultList();
	}	

	@GetMapping("/user/{userId}")
	public List<Quilt> getQuiltsForPerson(@PathVariable Long userId) {
		Person p = personService.getPerson(userId);
		
		if(p != null) {
			return p.getEntered();
		}
		
		return Collections.emptyList();
	}

	@GetMapping("/{id}")
	public Quilt getQuilt(@PathVariable Long id) {
		return quiltRepository.findById(id).get();
	}

	@PostMapping("/user/{userId}")
	public ResponseEntity<Quilt> createQuilt(@PathVariable Long userId, @RequestBody Quilt quilt) throws URISyntaxException {
		Person user = personService.getPerson(userId);
		return createQuilt(user, quilt);
	}

	@PostMapping
	public ResponseEntity<Quilt> createQuilt(Authentication auth, @RequestBody Quilt quilt) throws URISyntaxException {
		Person user = personService.getUser(auth.getName());
		return createQuilt(user, quilt);
	}
	
	private ResponseEntity<Quilt> createQuilt(Person user, Quilt quilt) throws URISyntaxException {
		if(user != null) {
			quilt.setNumber(nextQuiltNumber());
			quilt.setEnteredBy(user);
			quiltRepository.save(quilt);
			user.addQuilt(quilt);
			personService.save(user);
			return ResponseEntity.created(new URI(String.format("/quilts/%d", quilt.getId()))).body(quilt);
		}
		
		return null;
	}

	@PutMapping("/{id}")
	public ResponseEntity<Quilt> updateQuilt(@PathVariable Long id, @RequestBody Quilt quilt) {
		Quilt currentQuilt = quiltRepository.findById(id).orElse(null);
		
		if(currentQuilt == null) {
			return ResponseEntity.notFound().build();
		}
		
		Quilt updatedQuilt = quiltRepository.save(quilt);
		return ResponseEntity.ok(updatedQuilt);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteQuilt(@PathVariable Long id) {
		quiltRepository.deleteById(id);
		return ResponseEntity.ok().build();
	}
	
	
	@GetMapping("/total-due")
	public ResponseEntity<UserInvoice> getUserTotalDue(Authentication auth) {
		String email = auth.getName();
		
		Person user = personService.getUser(email);
		if(user == null) {
			return ResponseEntity.notFound().build();
		}

		List<Quilt> quiltsDue = paymentService.getUnpaidQuilts(user);
		UserInvoice userInvoice = new UserInvoice(paymentService.amountDue(user), quiltsDue.size());
		return ResponseEntity.ok(userInvoice);		
	}	
	
	@GetMapping("/pay")
	public ResponseEntity<String> payQuilts(Authentication auth) {
		String email = auth.getName();
		
		Person user = personService.getUser(email);
		if(user == null) {
			return ResponseEntity.notFound().build();
		}

		try {
			PaymentData paymentData = paymentService.createPayment(user);
			return ResponseEntity.ok(paymentData.getCheckoutUrl());
		}
		catch(PaymentException e) {
			log.error("Error fetching amount due for " + user.getEmail(), e);
			return ResponseEntity.internalServerError().body("Error encountered creating order: " + e.getMessage());
		}		
	}

	
	private int nextQuiltNumber() {
		return Integer.valueOf(nextQuiltNumber++);
	}

	/**
	 * Initialize the quilt number counter
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Integer currentLastQuiltNumber = quiltRepository.getMaxQuiltNumber();
		nextQuiltNumber = (currentLastQuiltNumber == null) ? STARTING_QUILT_NUMBER : (currentLastQuiltNumber + 1);
	}
}

@Data
@AllArgsConstructor
class UserInvoice {
	Double totalDue;
	int numQuilts;
}
