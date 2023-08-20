package org.eihq.quiltshow.reports;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eihq.quiltshow.exception.PaymentException;
import org.eihq.quiltshow.model.PaymentData;
import org.eihq.quiltshow.model.Person;
import org.eihq.quiltshow.model.Report;
import org.eihq.quiltshow.model.ReportResult;
import org.eihq.quiltshow.repository.PersonRepository;
import org.eihq.quiltshow.service.PaymentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaymentStatusReport extends Report {

	public static final Long ID = -10l;
	
	private static final PaymentStatusReport _singleton = new PaymentStatusReport();
	
	private static final String ENTRANT = "Entrant";
	private static final String ENTRIES = "Entry Count";
	private static final String TOTAL_DUE = "Total";
	private static final String TOTAL_DUE_WITH_OVERHEAD = "Total With Overhead";
	private static final String TOTAL_PAID = "Paid";
	private static final String PAYMENT_DATES = "Payment Date(s)";
	private static final String PAYMENTS = "Payment Links";
	
	PaymentService paymentService;
	
	
	private PaymentStatusReport() {

	}

	public static PaymentStatusReport instance() {
		return _singleton;
	}


	@Override
	public Long getId() {
		return ID;
	}

	@Override
	public String getReportName() {
		return "Quilt Payments";
	}


	@Override
	public ReportCategory getReportCategory() {
		return ReportCategory.MISCELLANEOUS;
	}
	
	@Override
	public String getFormat() {
		return "payment_status";
	}


	@Override
	public String getReportDescription() {
		return "Shows all submitted quilts and the current payment status";
	}


	@Override
	public List<String> getFields() {
		return Arrays.asList(ENTRANT, ENTRIES, TOTAL_DUE, TOTAL_DUE_WITH_OVERHEAD, TOTAL_PAID, PAYMENT_DATES, PAYMENTS);
	}
	
	
	public ReportResult run(PaymentService paymentService, PersonRepository personRepository) {
		log.debug("Starting Payment Status Report...");
		
		ReportResult result = new ReportResult();
		result.setDemo(false);
		result.setReport(this);
		result.setResults(new LinkedList<>());

		personRepository.findAll().forEach(person -> {
			if(!person.getEmail().equalsIgnoreCase("admin")) {
				Map<String,Object> row = new HashMap<>();
				row.put(ENTRANT, person.getFullName());
				row.put(ENTRIES, person.getEntered().size());
				row.put(TOTAL_DUE, paymentService.calculatePrice(person.getEntered()));
				row.put(TOTAL_DUE_WITH_OVERHEAD, paymentService.calculatePriceWithOverhead(person.getEntered()));
				
				try {
					row.put(TOTAL_PAID, calculateAmountPaid(person, paymentService));
				} catch (PaymentException e) {
					log.error(String.format("Unable to get total paid for %s", person.getFullName()), e);
					row.put(TOTAL_PAID, "Error");
				}
				
				try {
					row.put(PAYMENTS, getPaymentLinks(person, paymentService));
				} catch (PaymentException e) {
					log.error(String.format("Unable to get payment links for %s", person.getFullName()), e);
					row.put(TOTAL_PAID, Arrays.asList("Error"));
				}
				
				row.put(PAYMENT_DATES, getPaymentDates(person, paymentService));

				result.getResults().add(row);
			}
		});
		
		return result;
	}


	private Set<PaymentData> getPersonPayments(Person person) {
		Set<PaymentData> payments = new HashSet<>();
		
		person.getEntered().forEach(q -> {
			if((q.getPaymentData() != null) && !payments.contains(q.getPaymentData())) {
				payments.add(q.getPaymentData());
			}
		});

		return payments;
	}
	
	private Object getPaymentDates(Person person, PaymentService paymentService2) {
		Set<PaymentData> payments = getPersonPayments(person);
		
		return payments.stream().map(p -> p.getDateSubmitted()).collect(Collectors.toList());
	}


	private List<String> getPaymentLinks(Person person, PaymentService paymentService) throws PaymentException {
		Set<PaymentData> payments = getPersonPayments(person);
		List<String> links = new LinkedList<>();
		
		for(PaymentData payment : payments) {
			if(paymentService.paymentSuccess(payment)) {
				String link = paymentService.getPaymentLink(payment);
				if(link != null) {
					links.add(link);
				}
			}
		}
		
		return links;
	}


	private Double calculateAmountPaid(Person person, PaymentService paymentService) throws PaymentException {
		Set<PaymentData> payments = getPersonPayments(person);
			
		if(payments.isEmpty()) {
			return 0.0;
		}
		
		double totalPaid = 0.0;
		for(PaymentData payment : payments) {
			if(paymentService.paymentSuccess(payment)) {
				totalPaid += paymentService.paymentAmount(payment);
			}
		}
		
		return totalPaid;
	}
	
	
	
}

