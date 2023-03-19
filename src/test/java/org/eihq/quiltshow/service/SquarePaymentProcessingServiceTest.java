package org.eihq.quiltshow.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.eihq.quiltshow.exception.PaymentException;
import org.eihq.quiltshow.model.PaymentData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.squareup.square.api.CheckoutApi;
import com.squareup.square.exceptions.ApiException;
import com.squareup.square.models.CreatePaymentLinkRequest;
import com.squareup.square.models.CreatePaymentLinkResponse;
import com.squareup.square.models.Error;
import com.squareup.square.models.Money;
import com.squareup.square.models.PaymentLink;
import com.squareup.square.models.QuickPay;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(properties = {
    "payment.api.url=test.url",
	"payment.api.version=test",
	"payment.api.token=test_token",
	"payment.api.location=test_location",
	"payment.api.environment=test"
})
public class SquarePaymentProcessingServiceTest {
	
	private static final String TEST_PAYMENT_URL = "https://test.payment.url";

	private static final String TEST_PAYMENT_DATE = "2023-03-08T00:12:48Z";

	@Autowired
	SquarePaymentProcessingService service;
	
	@Captor
	ArgumentCaptor<CreatePaymentLinkRequest> requestCapture = ArgumentCaptor.forClass(CreatePaymentLinkRequest.class);
	
	CheckoutApi checkoutApi = Mockito.mock(CheckoutApi.class);

	@BeforeEach
	void setup() {
		service.checkoutApi = checkoutApi;
	}
	
	@Test 
	void createPaymentLinkRequest_shouldCreatePaymentLinkRequest_withParameters() {
		String name = "testName";
		String description = "test description";
		Double amount = 53.0;
		
		CreatePaymentLinkRequest request = service.createPaymentLinkRequest(name, description, amount);
		assertEquals(name, request.getQuickPay().getName());
		assertEquals(description, request.getDescription());
		assertTrue("Amount", amount.longValue() * 100 == request.getQuickPay().getPriceMoney().getAmount());
		assertEquals("test_location", request.getQuickPay().getLocationId());
	}
	
	@Test
	void submitPayment_shouldReturnPaymentData_forPaymentParameters() throws ApiException, IOException, PaymentException {
		String name = "testName";
		String description = "test description";
		Double amount = 53.0;
		String id = "testId";
		String orderId = "testOrderId";
		
		
		CreatePaymentLinkResponse response = mockResponse(id, orderId, description);
		when(checkoutApi.createPaymentLink(requestCapture.capture())).thenReturn(response);
		
		PaymentData data = service.submitPayment(name, description, amount);
		
		// verify the request values
		CreatePaymentLinkRequest request = requestCapture.getValue();
		assertNotNull(request);
		assertNotNull(request.getDescription());
		assertEquals(description, request.getDescription());
		assertNotNull(request.getQuickPay());
		
		QuickPay quickPay = request.getQuickPay();
		assertEquals("test_location", quickPay.getLocationId());
		assertEquals(name, quickPay.getName());
		assertNotNull(quickPay.getPriceMoney());
		
		Money priceMoney = quickPay.getPriceMoney();
		assertEquals("USD", priceMoney.getCurrency());
		assertEquals(Long.valueOf(amount.longValue() * 100), priceMoney.getAmount());
		
		
		// verify the response values
		assertNotNull(data);
		assertEquals(id, data.getPaymentProcessorId());
		assertEquals(orderId, data.getOrderId());
		
		//2023-03-08T00:12:48Z
		assertNotNull(data.getDateSubmitted());
		ZonedDateTime submitted = data.getDateSubmitted();
		assertEquals(2023, submitted.getYear());
		assertEquals(3, submitted.getMonthValue());
		assertEquals(8, submitted.getDayOfMonth());
		assertEquals(0, submitted.getHour());
		assertEquals(12, submitted.getMinute());
		assertEquals(48, submitted.getSecond());
		
		assertEquals(description, data.getDescription());
	}

	
	
	@Test
	void submitPayment_shouldThrowException_whenServiceReturnsErrors() throws ApiException, IOException {
		String name = "testName";
		String description = "test description";
		Double amount = 53.0;
		
		CreatePaymentLinkResponse response = mockResponse(Arrays.asList("error1", "error2"));
		when(checkoutApi.createPaymentLink(requestCapture.capture())).thenReturn(response);
		
		assertThrows(PaymentException.class, () -> service.submitPayment(name, description, amount));
	}


	
	private CreatePaymentLinkResponse mockResponse(String id, String orderId, String description) {
		PaymentLink paymentLink = new PaymentLink(
				2, 
				id, 
				description, 
				orderId, 
				null, null, 
				TEST_PAYMENT_URL, 
				TEST_PAYMENT_DATE, 
				null, null);
		CreatePaymentLinkResponse response = new CreatePaymentLinkResponse(Collections.emptyList(), paymentLink, null);
		
		return response;
	}
	
	private CreatePaymentLinkResponse mockResponse(List<String> errorMessages) {
		List<Error> errors = errorMessages.stream().map(e -> new Error(e, e, e, e)).collect(Collectors.toList());
		CreatePaymentLinkResponse response = new CreatePaymentLinkResponse(errors, null, null);
		
		return response;
	}
}
