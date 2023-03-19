package org.eihq.quiltshow.model;

import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
public class PaymentData {
	public enum Status {
		STARTED, IN_PROCESS, VERIFIED, ERROR, COMPLETED
	};

	@Id
    @GeneratedValue
	Long id;
	
	String serviceName;
	
	String description;
	
	String paymentProcessorId;
	
	String orderId;
	
	String checkoutUrl;
	
	@OneToMany(mappedBy = "paymentData")
	@JsonIgnore
	List<Quilt> quilts = new LinkedList<>();
	
	Status status;
	
	ZonedDateTime dateSubmitted;
	
	ZonedDateTime lastUpdated;	
	
}
