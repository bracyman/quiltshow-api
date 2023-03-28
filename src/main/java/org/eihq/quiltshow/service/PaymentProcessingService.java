package org.eihq.quiltshow.service;

import java.io.IOException;

import org.eihq.quiltshow.exception.PaymentException;
import org.eihq.quiltshow.model.PaymentData;
import org.eihq.quiltshow.model.PaymentData.Status;

import com.squareup.square.exceptions.ApiException;

public interface PaymentProcessingService {

	/**
	 * Returns the name of the payment processor
	 * @return
	 */
	String getServiceName();
	
	/**
	 * Submits a payment request to a payment processor. Returns payment data, including an ID unique to the 
	 * payment processor
	 * @param name
	 * @param description
	 * @param amount
	 * @return
	 * @throws PaymentException
	 */
	PaymentData submitPayment(String name, String description, Double amount) throws PaymentException;

	/**
	 * Retrieves the payment status from the payment processor, and converts it to a PaymentData Status
	 * @param paymentData
	 * @return
	 * @throws ApiException
	 * @throws IOException
	 */
	Status getPaymentStatus(PaymentData paymentData) throws PaymentException;

	/**
	 * Retrieves the amount paid in a transaction
	 * @param paymentData
	 * @return
	 */
	Double getPaymentTotal(PaymentData paymentData) throws PaymentException;

	/**
	 * Returns the link to view the payment/order in the payment service
	 * @param paymentData
	 * @return
	 */
	String getPaymentLink(PaymentData paymentData);
}
