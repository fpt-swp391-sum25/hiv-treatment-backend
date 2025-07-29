package backend.testorder.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.testorder.dto.CreateTestOrderRequest;
import backend.testorder.dto.UpdateTestOrderRequest;
import backend.testorder.model.TestOrder;
import backend.testorder.service.TestOrderService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/test-order")
@RequiredArgsConstructor
public class TestOrderController {
    private final TestOrderService testOrderService;

    @PostMapping()
    public ResponseEntity<Map<String, String>> create(@RequestBody CreateTestOrderRequest request) {
        return ResponseEntity.ok(Map.of("message", testOrderService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(@PathVariable long id, @RequestBody UpdateTestOrderRequest request) {
        return ResponseEntity.ok(Map.of("message", testOrderService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable long id) {
        return ResponseEntity.ok(Map.of("message", testOrderService.delete(id)));
    }

    @GetMapping("/health-record-id/{healthRecordId}")
    public ResponseEntity<List<TestOrder>> get(@PathVariable long healthRecordId) {
        return ResponseEntity.ok(testOrderService.list(healthRecordId));
    }

    @PutMapping("/success/{healthRecordId}")
    public ResponseEntity<Map<String, String>> confirmPayment(@PathVariable long healthRecordId) {
        return ResponseEntity.ok(Map.of("message", testOrderService.confirmPayment(healthRecordId)));
    }

    @PutMapping("/undo/{healthRecordId}")
    public ResponseEntity<Map<String, String>> undoPayment(@PathVariable long healthRecordId) {
        return ResponseEntity.ok(Map.of("message", testOrderService.undoPayment(healthRecordId)));
    }
}
