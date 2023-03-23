package org.eihq.quiltshow.service;


import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

import org.eihq.quiltshow.exception.PaymentException;
import org.eihq.quiltshow.model.PaymentData;
import org.eihq.quiltshow.model.PaymentData.Status;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.squareup.square.Environment;
import com.squareup.square.SquareClient;
import com.squareup.square.api.CheckoutApi;
import com.squareup.square.api.OrdersApi;
import com.squareup.square.exceptions.ApiException;
import com.squareup.square.models.CreatePaymentLinkRequest;
import com.squareup.square.models.CreatePaymentLinkResponse;
import com.squareup.square.models.GetPaymentResponse;
import com.squareup.square.models.Money;
import com.squareup.square.models.QuickPay;
import com.squareup.square.models.RetrieveOrderResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SquarePaymentProcessingService implements PaymentProcessingService,InitializingBean {

	
	public static final DateTimeFormatter rfc3339Formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS]XXX")
            .withResolverStyle(ResolverStyle.LENIENT);

	
	@Value("${payment.api.url:connect.squareup.com}")
	String squareApiUrl;
	
	@Value("${payment.api.version:v2}")
	String squareApiVersion;
	
	@Value("${payment.api.token:EAAAEH6Wq7LG1Pu-Fn9NcI-1mLVjQXsWGE5J1CnSZq62aSboFKquTy89y4RzbsqW}")
	String accessToken;
	
	@Value("${payment.api.location:LMKYR87PKEPVB}")
	String location;
	
	@Value("${payment.api.environment:sandbox}")
	String environment;
	
	SquareClient squareClient;
	
	CheckoutApi checkoutApi;
	
	OrdersApi ordersApi;

	
	public PaymentData submitPayment(String name, String description, Double amount) throws PaymentException {
		CreatePaymentLinkRequest paymentRequest = createPaymentLinkRequest(name, description, amount);
		
		try {
			CreatePaymentLinkResponse response = checkoutApi.createPaymentLink(paymentRequest);
			
			if((response.getErrors() == null) || response.getErrors().isEmpty()) {
				log.info("Square order [{}] created", response.getPaymentLink().getOrderId());
				
				PaymentData paymentData = new PaymentData();
				paymentData.setServiceName(getServiceName());
				paymentData.setDescription(response.getPaymentLink().getDescription());
				paymentData.setPaymentProcessorId(response.getPaymentLink().getId());
				paymentData.setOrderId(response.getPaymentLink().getOrderId());
				paymentData.setDateSubmitted(ZonedDateTime.parse(response.getPaymentLink().getCreatedAt(), rfc3339Formatter));
				paymentData.setCheckoutUrl(response.getPaymentLink().getUrl());
				paymentData.setStatus(Status.IN_PROCESS);

				return paymentData;
			}
			else {
				StringBuilder errMessage = new StringBuilder("Errors encountered trying to create payment with Square:\n");
				response.getErrors().forEach(err -> errMessage
						.append("  : ")
						.append(err.getCode()).append(" - ")
						.append(err.getDetail()).append("\n")
					);
				PaymentException ex = new PaymentException(errMessage.toString());
				log.error(ex.getMessage());
				throw ex;
			}
		} catch (ApiException | IOException e) {
			PaymentException ex = new PaymentException("Failed to create payment with Square", e);
			log.error(ex.getMessage(), ex);
			throw ex;
		}
	}

	@Override
	public Status getPaymentStatus(PaymentData paymentData) throws PaymentException {
		if(paymentData == null) {
			return Status.ERROR;
		}
		
		GetPaymentResponse paymentResponse = null;
		try {
			RetrieveOrderResponse orderResponse = ordersApi.retrieveOrder(paymentData.getOrderId());
			
			if((orderResponse.getOrder().getTenders() == null) || orderResponse.getOrder().getTenders().isEmpty()) {
				log.trace("No tender submitted for order {}", paymentData.getOrderId());
				return Status.ERROR;
			}
			
			String paymentId = orderResponse.getOrder().getTenders().get(0).getPaymentId();
			log.debug("Loading payment status for payment {}", paymentId);
			paymentResponse = squareClient.getPaymentsApi().getPayment(paymentId);
		} catch (ApiException | IOException e) {
			throw new PaymentException(e.getMessage(), e);
		}
		
		if(paymentResponse == null) {
			return Status.ERROR;
		}
		
		String status = paymentResponse.getPayment().getStatus();
		
		if("APPROVED".equalsIgnoreCase(status)) {
			return Status.VERIFIED;
		}
		else if("COMPLETED".equalsIgnoreCase(status)) {
			return Status.COMPLETED;
		}
		else if("PENDING".equalsIgnoreCase(status)) {
			return Status.IN_PROCESS;
		}
		else if("CANCELED".equalsIgnoreCase(status)) {
			return Status.ERROR;
		}
		else if("FAILED".equalsIgnoreCase(status)) {
			return Status.ERROR;
		}
		
		return Status.ERROR;
	}
	
	protected CreatePaymentLinkRequest createPaymentLinkRequest(String name, String description, Double amount) {
		CreatePaymentLinkRequest paymentRequest = new CreatePaymentLinkRequest.Builder()
				.description(description)
				.quickPay(new QuickPay(
						name,
						new Money(amount.longValue() * 100, "USD"),			// square takes USD currency as cents
						location))
				.build();
		
		return paymentRequest;
	}
	
	
	/**
	 * Initialize the connection to the payment service
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		squareClient = new SquareClient.Builder()
				.environment(Environment.fromString(environment))
				.accessToken(accessToken)
				.build();
		
		checkoutApi = squareClient.getCheckoutApi();
		
		ordersApi = squareClient.getOrdersApi();
	}


	@Override
	public String getServiceName() {
		return "Square";
	}

}
