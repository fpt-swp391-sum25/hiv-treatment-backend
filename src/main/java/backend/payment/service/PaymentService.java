package backend.payment.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import backend.payment.dto.CreatePaymentRequest;
import backend.payment.model.Payment;
import backend.payment.repository.PaymentRepository;
import backend.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {
    @Autowired
    private final PaymentRepository paymentRepository;

    @Autowired
    private final ScheduleRepository checkupScheduleRepository;

    @Autowired
    private final VNPayService vnpayService;

    // Create payment
    public String create(CreatePaymentRequest request) {
        Payment payment = Payment.builder()
                .description(request.description())
                .time(request.time())
                .amount(request.amount())
                .schedule(checkupScheduleRepository.findById(request.scheduleId()).get())
                .build();
        paymentRepository.save(payment);

        return "PAYMENT CREATED SUCCESSFULLY WITH ID: " + payment.getId();
    }

    // Initiate payment
    public String initiatePayment(Long scheduleId, String amount, String ipAddress)
            throws UnsupportedEncodingException, Exception {
        return vnpayService.createPaymentUrl(scheduleId, amount, ipAddress);
    }

    // List payments
    public List<Payment> list() {
        return paymentRepository.findAll();
    }

    // Read payment detail
    public Payment get(long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NO PAYMENT FOUND WITH ID: " + id));
    }

    // List payments by status
    public List<Payment> getByStatus(String status) {
        return paymentRepository.findByStatus(status);
    }

    public void deleteByScheduleId(long scheduleId) {
        paymentRepository.deleteByScheduleId(scheduleId);
    }
}