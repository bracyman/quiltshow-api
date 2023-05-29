package org.eihq.quiltshow.model;

import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class PaymentData {
	public enum Status {
		STARTED, IN_PROCESS, VERIFIED, ERROR, COMPLETED
	};

	@Id
    @GeneratedValue
	Long id;
	
	String serviceName;
	
	String description;
	
	Double amount;
	
	String paymentProcessorId;
	
	String orderId;
	
	String checkoutUrl;
	
	@OneToMany(mappedBy = "paymentData")
	@JsonIgnore
	@EqualsAndHashCode.Exclude
	List<Quilt> quilts = new LinkedList<>();
	
	Status status;
	
	ZonedDateTime dateSubmitted;
	
	ZonedDateTime lastUpdated;	
	
}
