package backend.payment.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import backend.payment.dto.CashPaymentRequest;
import backend.payment.dto.PaymentDTO;
import backend.payment.dto.SearchPaymentRequest;
import backend.payment.model.Payment;
import backend.payment.service.PaymentService;
import backend.payment.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final VNPayService vnpayService;

    @PostMapping()
    public ResponseEntity<String> initiatePayment(@RequestBody PaymentDTO paymentDTO, HttpServletRequest request)
            throws UnsupportedEncodingException, Exception {
        String ipAddress = vnpayService.getIpAddress(request);
        String paymentUrl = paymentService.initiatePayment(paymentDTO.getScheduleId(), paymentDTO.getAmount(),
                ipAddress);
        return ResponseEntity.ok(paymentUrl);
    }

    @PostMapping("/retry")
    public ResponseEntity<String> retryPayment(@RequestBody PaymentDTO paymentDTO, HttpServletRequest request)
            throws UnsupportedEncodingException, Exception {
        String ipAddress = vnpayService.getIpAddress(request);
        String paymentUrl = paymentService.retryPayment(paymentDTO.getScheduleId(), paymentDTO.getAmount(),
                ipAddress);
        return ResponseEntity.ok(paymentUrl);
    }

    @PostMapping("/cash")
    public ResponseEntity<?> createCashPayment(@RequestBody CashPaymentRequest request) {
        paymentService.createCashPayment(request);
        return ResponseEntity.ok("Tạo thanh toán tiền mặt thành công");
    }

    @GetMapping("/callback")
    public ResponseEntity<String> paymentCallback(@RequestParam Map<String, String> params) {
        try {
            vnpayService.handlePaymentCallback(params);
            return ResponseEntity.ok("Thanh toán thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Thanh toán thất bại: " + e.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<List<Payment>> list() {
        return ResponseEntity.ok(paymentService.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> list(@PathVariable long id) {
        return ResponseEntity.ok(paymentService.get(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Payment>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(paymentService.getByStatus(status));
    }

    @PostMapping("/search")
    public ResponseEntity<List<Payment>> searchByStatusAndPatientName(
            @RequestBody SearchPaymentRequest searchPaymentRequest) {
        return ResponseEntity.ok(paymentService.getPaymentsByFilter(searchPaymentRequest));
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<Payment> togglePaymentStatus(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.togglePaymentStatus(id));
    }

    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<Payment> getByScheduleId(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(paymentService.getByScheduleId(scheduleId));
    }

    @GetMapping("/statistics")
    public ResponseEntity<List<Payment>> getPaymentStatistics(
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate) {
            
        System.out.println("Received start = " + startDate);
        System.out.println("Received end = " + endDate);

        return ResponseEntity.ok(paymentService.getPaymentStatistics(startDate, endDate));
    }
}
