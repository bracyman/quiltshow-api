package org.eihq.quiltshow.repository;

import org.eihq.quiltshow.model.PaymentData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentDataRepository extends JpaRepository<PaymentData, Long> {
    
}
