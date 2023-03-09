package org.eihq.quiltshow.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eihq.quiltshow.exception.PaymentException;
import org.eihq.quiltshow.model.PaymentData;
import org.eihq.quiltshow.model.PaymentData.Status;
import org.eihq.quiltshow.model.Person;
import org.eihq.quiltshow.model.Quilt;
import org.eihq.quiltshow.repository.PaymentDataRepository;
import org.eihq.quiltshow.repository.QuiltRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.squareup.square.exceptions.ApiException;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class PaymentServiceTest {

	@Autowired
	PaymentService service;

	@Mock
	PaymentDataRepository paymentDataRepository;
	
	@Mock
	QuiltRepository quiltRepository;
	
	@Mock
	PaymentProcessingService paymentProcessingService;

	
	@Captor
	ArgumentCaptor<String> nameCapture = ArgumentCaptor.forClass(String.class);
	
	@Captor
	ArgumentCaptor<String> descriptionCapture = ArgumentCaptor.forClass(String.class);
	
	@Captor
	ArgumentCaptor<Double> amountCapture = ArgumentCaptor.forClass(Double.class);
	
	
	@BeforeEach
	void setup() {
		service.paymentProcessingService = paymentProcessingService;
		service.paymentDataRepository = paymentDataRepository;
		service.quiltRepository = quiltRepository;
		
		when(paymentDataRepository.save(any(PaymentData.class))).thenReturn(new PaymentData());
		when(quiltRepository.save(any(Quilt.class))).thenReturn(new Quilt());
	}
	

	@Test
	void amountDue_shouldReturnZero_whenNoQuiltsExist() {
		Person p = new Person();
		p.setEntered(null);
		
		Double due = service.amountDue(p);
		assertNotNull(due);
		assertEquals(0.0, due.doubleValue(), 0.0);
		
		p.setEntered(Collections.emptyList());
		
		due = service.amountDue(p);
		assertNotNull(due);
		assertEquals(0.0, due.doubleValue(), 0.0);

	}
	
	
	@Test
	void amountDue_shouldReturnLowerPrice_whenQuiltNotJudged() {
		Person p = new Person();
		Quilt q = new Quilt();
		q.setJudged(false);
		p.addQuilt(q);
		
		Double due = service.amountDue(p);
		assertNotNull(due);
		assertEquals(6.0, due.doubleValue(), 0.0);

		q = new Quilt();
		q.setJudged(false);
		p.addQuilt(q);
		
		due = service.amountDue(p);
		assertNotNull(due);
		assertEquals(11.0, due.doubleValue(), 0.0);

	}
	
	
	@Test
	void amountDue_shouldReturnHigherPrice_whenQuiltJudged() {
		Person p = new Person();
		Quilt q = new Quilt();
		q.setJudged(true);
		p.addQuilt(q);
		
		Double due = service.amountDue(p);
		assertNotNull(due);
		assertEquals(11.0, due.doubleValue(), 0.0);

		q = new Quilt();
		q.setJudged(true);
		p.addQuilt(q);
		
		due = service.amountDue(p);
		assertNotNull(due);
		assertEquals(21.0, due.doubleValue(), 0.0);
	}
	
	
	@Test
	void amountDue_shouldCalculateSquareMarkup_afterSubtotal() {
		Person p = new Person();
		Quilt q = new Quilt();
		q.setJudged(true);
		p.addQuilt(q);
		
		q = new Quilt();
		q.setJudged(true);
		p.addQuilt(q);
		
		q = new Quilt();
		q.setJudged(false);
		p.addQuilt(q);
		
		q = new Quilt();
		q.setJudged(false);
		p.addQuilt(q);
		
		q = new Quilt();
		q.setJudged(true);
		p.addQuilt(q);
		
		// if incorrect, we'll get ceil(10.59 + 10.59 + 5.45 + 5.45 + 10.59)
		// if correct, we'll get(ceil(.3 + 1.029 * (10 + 10 + 5 + 5 + 10))
		Double due = service.amountDue(p);
		assertNotNull(due);
		assertEquals(42.0, due.doubleValue(), 0.0);
	}
	
	
	@Test
	void amountDue_shouldIgnoreQuilts_alreadyPaid() {
		Person p = new Person();
		Quilt q = new Quilt();
		q.setJudged(true);
		p.addQuilt(q);
		
		q = new Quilt();
		q.setJudged(true);
		q.setPaymentData(new PaymentData());
		q.getPaymentData().setStatus(Status.IN_PROCESS);
		p.addQuilt(q);
		
		Double due = service.amountDue(p);
		assertNotNull(due);
		assertEquals(11.0, due.doubleValue(), 0.0);
	}
	
	
	@Test
	void pendingDue_shouldIgnoreQuilts_notAlreadyPaid() {
		Person p = new Person();
		Quilt q = new Quilt();
		q.setJudged(true);
		p.addQuilt(q);
		
		q = new Quilt();
		q.setJudged(true);
		p.addQuilt(q);
		
		q = new Quilt();
		q.setJudged(true);
		q.setPaymentData(new PaymentData());
		q.getPaymentData().setStatus(Status.IN_PROCESS);
		p.addQuilt(q);
		
		Double due = service.pendingDue(p);
		assertNotNull(due);
		assertEquals(11.0, due.doubleValue(), 0.0);
	}
	
	@Test
	void createPayment_shouldReturnPaymentData_forAllQuilts() throws ApiException, IOException, PaymentException {
		String fname = "Quilt", lname = "Master";
		String id = "testId",
				orderId = "testOrderId",
				description = "the description";
		PaymentData response = mockResponse(id, orderId, description);
		
		when(paymentProcessingService.submitPayment
				(nameCapture.capture(),  descriptionCapture.capture(), amountCapture.capture())
			)
			.thenReturn(response);
		

		// create a person
		Person p = new Person();
		p.setFirstName(fname);
		p.setLastName(lname);
		
		// create some quilts
		List<Quilt> quilts = new LinkedList<>();
		Quilt q = new Quilt();
		q.setJudged(true);
		q.setEnteredBy(p);
		quilts.add(q);
		
		q = new Quilt();
		q.setJudged(true);
		q.setEnteredBy(p);
		quilts.add(q);
		
		q = new Quilt();
		q.setJudged(true);
		q.setEnteredBy(p);
		quilts.add(q);

		PaymentData data = service.createPayment(quilts);
		
		// verify the request values
		String requestName = nameCapture.getValue();
		String requestDescription = descriptionCapture.getValue();
		Double requestAmount = amountCapture.getValue();
		assertNotNull(requestName);
		assertNotNull(requestDescription);
		assertNotNull(requestAmount);
		
		assertTrue("Request Name", 
				requestName.contains("EIHQ Quilt Show - Registering")
				&& requestName.contains("%d".formatted(quilts.size())));
		assertTrue("Request Description", 
				requestDescription.contains(p.getFullName())
				&& requestDescription.contains("%d".formatted(quilts.size()))
				&& requestDescription.contains("%s".formatted(" quilts for registration")));
		assertEquals(Double.valueOf(32.0), requestAmount);
		
		// verify the response values
		assertNotNull(data);
		assertEquals(id, data.getPaymentProcessorId());
		assertEquals(orderId, data.getOrderId());
		assertEquals(description, data.getDescription());
		assertEquals(quilts.size(), data.getQuilts().size());
	}
	
	
	
	
	private PaymentData mockResponse(String id, String orderId, String description) {
		PaymentData response = new PaymentData();
		response.setPaymentProcessorId(id);
		response.setOrderId(orderId);
		response.setDescription(description);
		response.setServiceName("test");
		response.setStatus(Status.IN_PROCESS);
		
		return response;
	}
}
