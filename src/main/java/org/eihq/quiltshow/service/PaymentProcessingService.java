package org.eihq.quiltshow.service;

import org.eihq.quiltshow.exception.PaymentException;
import org.eihq.quiltshow.model.PaymentData;

public interface PaymentProcessingService {

	/**
	 * Returns the name of the payment processor
	 * @return
	 */
	public String getServiceName();
	
	/**
	 * Submits a payment request to a payment processor. Returns payment data, including an ID unique to the 
	 * payment processor
	 * @param name
	 * @param description
	 * @param amount
	 * @return
	 * @throws PaymentException
	 */
	public PaymentData submitPayment(String name, String description, Double amount) throws PaymentException;
}
