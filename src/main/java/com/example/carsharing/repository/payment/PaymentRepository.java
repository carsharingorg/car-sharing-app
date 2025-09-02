package com.example.carsharing.repository.payment;

import com.example.carsharing.model.payment.Payment;
import com.example.carsharing.model.payment.Status;
import com.example.carsharing.model.payment.Type;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p FROM Payment p where p.rental.user.id = :userId")
    List<Payment> findAllByUserId(Long userId);

    @Query("select p from Payment p where p.session = :sessionId")
    Optional<Payment> findBySessionId(String sessionId);

    Optional<Payment> findByRentalIdAndTypeAndStatus(Long id, Type type, Status pending);
}
