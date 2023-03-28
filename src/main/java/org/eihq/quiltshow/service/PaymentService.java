package org.eihq.quiltshow.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.eihq.quiltshow.exception.PaymentException;
import org.eihq.quiltshow.model.PaymentData;
import org.eihq.quiltshow.model.PaymentData.Status;
import org.eihq.quiltshow.model.Person;
import org.eihq.quiltshow.model.Quilt;
import org.eihq.quiltshow.repository.PaymentDataRepository;
import org.eihq.quiltshow.repository.QuiltRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;


/**
 * Provides functions to calculate the total due for a person who submitted quilts, and the ability to 
 * create a new order in Square
 * @author bracyman
 *
 */
@Service
@Slf4j
public class PaymentService {

	
	@Autowired
	PaymentProcessingService paymentProcessingService;
	
	@Autowired
	PaymentDataRepository paymentDataRepository;
	
	@Autowired
	QuiltRepository quiltRepository;
	
	/**
	 * Calculates the amount due for quilts submitted. This does not include quilts that already have
	 * a payment in process
	 * @param person
	 * @return
	 */
	public Double amountDue(Person person) {
		return calculatePrice(getUnpaidQuilts(person));
	}
	
	/**
	 * Calculates the amount due that has already been submitted to Square for processing
	 * @param person
	 * @return
	 */
	public Double pendingDue(Person person) {
		if((person.getEntered() == null) || person.getEntered().isEmpty()) {
			return 0.0;
		}
		
		return calculatePrice(person.getEntered().stream()
				.filter(q -> (q.getPaymentData() != null) && (q.getPaymentData().getStatus() == Status.IN_PROCESS))
				.collect(Collectors.toList()));
	}
	
	
	/**
	 * Creates a payment for all unpaid quilts entered by the person
	 * @param person
	 * @return
	 * @throws PaymentException
	 */
	public PaymentData createPayment(Person person) throws PaymentException {
		return createPayment(getUnpaidQuilts(person));
	}
	
	
	/**
	 * Creates a payment link in Square, and returns the payment data (including a link to the checkout page)
	 * @param quilts
	 * @return
	 */
	public PaymentData createPayment(List<Quilt> quilts) throws PaymentException {
		if((quilts == null) || quilts.isEmpty()) {
			return null;
		}
				
		log.info(String.format("Submitting payment request for %d quilts", quilts.size()));
		PaymentData paymentData = paymentProcessingService.submitPayment(
				buildPaymentName(quilts), 
				buildPaymentDescription(quilts), 
				calculatePrice(quilts));
				
		paymentData.setQuilts(quilts);
		paymentDataRepository.save(paymentData);
		log.info(String.format("Payment %s request created", paymentData.getOrderId()));
		
		// update the quilt payment records
		quilts.forEach(q -> {
			q.setPaymentData(paymentData);
		});
		quiltRepository.saveAll(quilts);
		
		return paymentData;
	}

	/**
	 * Returns any quilts submitted by a person that have do not have a payment object associated with them
	 * @param person
	 * @return
	 */
	public List<Quilt> getUnpaidQuilts(Person person) {
		if((person.getEntered() == null) || person.getEntered().isEmpty()) {
			return Collections.emptyList();
		}
		
		Map<Long, Boolean> notPaid = new ConcurrentHashMap<>();
		person.getEntered().forEach(q -> {
			if((q.getPaymentData() != null) && !notPaid.containsKey(q.getPaymentData().getId())) {
				notPaid.put(q.getPaymentData().getId(), !paymentSuccess(q.getPaymentData()));
			}
		});
		
		return person.getEntered().stream()
				.filter(q -> (q.getPaymentData() == null) || notPaid.getOrDefault(q.getPaymentData().getId(), false))
				.collect(Collectors.toList());
	}
	
	/**
	 * Determines if the quilt payment has been approved or completed
	 * @param paymentData
	 * @return
	 * @throws PaymentException 
	 */
	public boolean paymentSuccess(PaymentData paymentData) {
		Status status;
		try {
			status = paymentProcessingService.getPaymentStatus(paymentData);
			return (status == Status.COMPLETED) || (status == Status.VERIFIED) || (status == Status.IN_PROCESS);  
		} catch (PaymentException e) {
			log.error(String.format("Unable to determine payment status for payment data %d", paymentData.getId()), e);
			return false;
		}
	}
	
	
	/**
	 * Fetches the total paid for the payment record
	 * @param paymentData
	 * @return
	 * @throws PaymentException 
	 */
	public Double paymentAmount(PaymentData paymentData) throws PaymentException {
		return paymentProcessingService.getPaymentTotal(paymentData);
	}

	/**
	 * Returns the total price to register all of the quilts in the list
	 * @param quilts
	 * @return
	 */
	public double calculatePrice(List<Quilt> quilts) {
		if((quilts == null) || quilts.isEmpty()) {
			return 0.0;
		}
		
		double subTotal = quilts.stream().map(q -> quiltPrice(q)).reduce(0.0, (total, price) -> total + price);
		return calculateOverhead(subTotal);
	}
	
	/**
	 * Calculates the square cost and adds it to the total
	 * @param subtotal
	 * @return
	 */
	private double calculateOverhead(double subtotal) {
		return Math.ceil(0.3 + 1.029 * subtotal);
	}
	
	/**
	 * Based on the quilt options, determines the cost of entry for the quilt
	 * @param q
	 * @return
	 */
	public Double quiltPrice(Quilt q) {
		if(q == null) {
			return 0.0;
		}
		
		return q.getJudged() ? 10.0 : 5.0;
	}

	/**
	 * COnstructs a name label for the checkout page
	 * @param quilts
	 * @return
	 */
	private String buildPaymentName(List<Quilt> quilts) {
		return String.format("EIHQ Quilt Show - Registering %d quilts", quilts.size());
	}

	/**
	 * Returns a description of the payment request
	 * @param quilts
	 * @return
	 */
	private String buildPaymentDescription(List<Quilt> quilts) {
		return String.format("[%s] submitting %d quilts for registration", 
				quilts.stream().map(q -> q.getEnteredBy().getFullName()).collect(Collectors.joining(",")),
				quilts.size()
			);
	}

	/**
	 * Returns the link to view the payment/order in the payment service
	 * @return
	 */
	public String getPaymentLink(PaymentData paymentData) {
		return paymentProcessingService.getPaymentLink(paymentData);
	}

}
