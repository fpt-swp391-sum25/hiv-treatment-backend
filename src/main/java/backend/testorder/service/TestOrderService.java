package backend.testorder.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import backend.healthrecord.model.HealthRecord;
import backend.healthrecord.repository.HealthRecordRepository;
import backend.testorder.dto.CreateTestOrderRequest;
import backend.testorder.dto.UpdateTestOrderRequest;
import backend.testorder.model.TestOrder;
import backend.testorder.repository.TestOrderRepository;
import backend.testtype.model.TestType;
import backend.testtype.repository.TestTypeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TestOrderService {
    @Autowired
    private final TestOrderRepository testOrderRepository;

    @Autowired
    private final HealthRecordRepository healthRecordRepository;

    @Autowired
    private final TestTypeRepository testTypeRepository;

    // Create test order
    public String create(CreateTestOrderRequest request) {
        var testOrder = TestOrder.builder()
                .name(request.name())
                .note(request.note())
                .paymentStatus("Chưa thanh toán")
                .expectedResultTime(request.expectedResultTime())
                .healthRecord(healthRecordRepository.findById(request.healthRecordId()).get())
                .build();
        testOrderRepository.save(testOrder);

        return "TEST ORDER CREATED SUCCESSFULLY WITH ID: " + testOrder.getId();
    }

    // List test order
    public List<TestOrder> list() {
        return testOrderRepository.findAll();
    }

    // Update test order
    public String update(long id, UpdateTestOrderRequest request) {
        TestOrder testOrder = testOrderRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NO TEST ORDER FOUND WITH ID: " + id));
        TestType testType = testTypeRepository.findById(request.testTypeId()).get();
        Optional.ofNullable(request.name()).ifPresent(testOrder::setName);
        Optional.ofNullable(request.result()).ifPresent(testOrder::setResult);
        Optional.ofNullable(request.unit()).ifPresent(testOrder::setUnit);
        Optional.ofNullable(request.note()).ifPresent(testOrder::setNote);
        Optional.ofNullable(testTypeRepository.findById(request.testTypeId()).get()).ifPresent(testOrder::setType);
        Optional.ofNullable(request.paymentStatus()).ifPresent(testOrder::setPaymentStatus);
        Optional.ofNullable(request.expectedResultTime()).ifPresent(testOrder::setExpectedResultTime);
        Optional.ofNullable(request.actualResultTime()).ifPresent(testOrder::setActualResultTime);
        testOrderRepository.save(testOrder);

        return "TEST ORDER UPDATED SUCCESSFULLY WITH ID: " + id;
    }

    // Delete test order
    public String delete(long id) {
        testOrderRepository.delete(testOrderRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NO TEST ORDER FOUND WITH ID: " + id)));

        return "TEST ORDER DELETED SUCCESSFULLY WITH ID: " + id;
    }

    // List test orders by health record ID
    public List<TestOrder> list(long recordId) {
        return testOrderRepository.findByHealthRecordId(recordId);
    }

    // Confirm payment of test order by health record ID
    public String confirmPayment(long healthRecordId, String totalPrice) {
        List<TestOrder> testOrders = testOrderRepository.findByHealthRecordId(healthRecordId);
        HealthRecord healthRecord = healthRecordRepository.findById(healthRecordId).get();
        healthRecord.setTestOrderPrice(Float.parseFloat(totalPrice));
        if (testOrders.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "NO TEST ORDER FOUND WITH HEALTH RECORD ID: " + healthRecordId);
        }

        for (TestOrder order : testOrders) {
            order.setPaymentStatus("Đã thanh toán");
        }
        healthRecordRepository.save(healthRecord);
        testOrderRepository.saveAll(testOrders);
        return "CONFIRM TEST PAYMENT SUCCESSFULLY WITH SCHEDULE ID: " + healthRecordId;
    }

    // Undo payment of test order by health record ID
    public String undoPayment(long healthRecordId) {
        HealthRecord healthRecord = healthRecordRepository.findById(healthRecordId).get();
        healthRecord.setTestOrderPrice(0);
        List<TestOrder> testOrders = testOrderRepository.findByHealthRecordId(healthRecordId);

        if (testOrders.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "NO TEST ORDER FOUND WITH HEALTH RECORD ID: " + healthRecordId);
        }

        for (TestOrder order : testOrders) {
            order.setPaymentStatus("Chưa thanh toán");
        }
        healthRecordRepository.save(healthRecord);
        testOrderRepository.saveAll(testOrders);
        return "UNDO TEST PAYMENT SUCCESSFULLY WITH SCHEDULE ID: " + healthRecordId;
    }
}
