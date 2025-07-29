package backend.payment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend.payment.model.Payment;
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByScheduleId(long scheduleId);

    List<Payment> findByStatus(String status);

    Optional<Payment> findByPaymentRef(String paymentRef);

    @Query("""
    SELECT p FROM Payment p
    LEFT JOIN p.schedule s
    LEFT JOIN s.patient u
    WHERE p.status = :status
    AND (:name IS NULL OR (u IS NOT NULL AND u.fullName LIKE CONCAT('%', :name, '%')))
    AND (:description IS NULL OR p.description LIKE CONCAT('%', :description, '%'))
    """)
    List<Payment> findByFilter(
        @Param("status") String status,
        @Param("name") String name,
        @Param("description") String description
    );
}   
