package backend.payment.service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import backend.payment.dto.CashPaymentRequest;
import backend.payment.dto.CreatePaymentRequest;
import backend.payment.dto.SearchPaymentRequest;
import backend.payment.model.Payment;
import backend.payment.repository.PaymentRepository;
import backend.schedule.model.Schedule;
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

    public String retryPayment(Long scheduleId, String amount, String ipAddress)
            throws UnsupportedEncodingException, Exception {

        return vnpayService.retryPayment(scheduleId, amount, ipAddress);
    }

    // List payments
    public List<Payment> list() {
        return paymentRepository.findAll();
    }

    // Read payment detail
    public Payment get(long id) {
        return paymentRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NO PAYMENT FOUND WITH ID: " + id));
    }

    // List payments by status
    public List<Payment> getByStatus(String status) {
        return paymentRepository.findByStatus(status);
    }

    // Delete shchedule by Id
    public String deletePaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NO PAYMENT FOUND WITH ID: " + id));

        paymentRepository.delete(payment);
        
        return "PAYMENT DELETED SUCCESSFULLY WITH ID: " + id;
    }

    // View payment status
    public Payment togglePaymentStatus(Long id) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PAYMENT NOT FOUND"));

        String currentStatus = payment.getStatus();
        if ("Chờ thanh toán".equals(currentStatus)) {
            payment.setStatus("Đã thanh toán");
        } else if ("Đã thanh toán".equals(currentStatus)) {
            payment.setStatus("Chờ thanh toán");
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID STATUS" + currentStatus);
        }

        return paymentRepository.save(payment);
    }

    // Search payment 
    public List<Payment> getPaymentsByFilter(SearchPaymentRequest searchPaymentRequest) {
        String trimmedName = (searchPaymentRequest.name() != null && !searchPaymentRequest.name().trim().isEmpty()) ? searchPaymentRequest.name().trim() : null;
        String trimmedDesc = (searchPaymentRequest.description() != null && !searchPaymentRequest.description().trim().isEmpty()) ? searchPaymentRequest.description().trim() : null;

        return paymentRepository.findByFilter(searchPaymentRequest.status(), trimmedName, trimmedDesc);
    }

    // Get payment by schedule ID
    public Payment getByScheduleId(Long scheduleId) {
        return paymentRepository.findByScheduleId(scheduleId)
                .orElseThrow(() -> new RuntimeException("PAYMENT NOT FOUND WITH SCHEDULE ID: " + scheduleId));
    }

    public void createCashPayment(CashPaymentRequest request) {
        Schedule schedule = checkupScheduleRepository.findById(request.scheduleId())
                .orElseThrow(() -> new RuntimeException("SCHEDULE NOT FOUND"));

        Payment payment = new Payment();
        payment.setSchedule(schedule);
        payment.setAmount(request.amount());
        payment.setDescription("Tiền mặt");
        payment.setStatus("Chờ thanh toán");
        payment.setTime(LocalDateTime.now());

        paymentRepository.save(payment);
    }
}