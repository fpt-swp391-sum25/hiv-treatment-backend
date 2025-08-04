package backend.testtype.controller;

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

import backend.testtype.dto.CreateTestTypeRequest;
import backend.testtype.dto.UpdateTestTypeRequest;
import backend.testtype.model.TestType;
import backend.testtype.service.TestTypeService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/test-type")
@RequiredArgsConstructor
public class TestTypeController {
    private final TestTypeService testTypeService;

    @PostMapping()
    public ResponseEntity<Map<String, String>> create(@RequestBody CreateTestTypeRequest request) {
        return ResponseEntity.ok(Map.of("message", testTypeService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(@PathVariable long id,
            @RequestBody UpdateTestTypeRequest request) {
        return ResponseEntity.ok(Map.of("message", testTypeService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable long id) {
        return ResponseEntity.ok(Map.of("message", testTypeService.delete(id)));
    }

    @GetMapping()
    public ResponseEntity<List<TestType>> list() {
        return ResponseEntity.ok(testTypeService.list());
    }

    @PostMapping("/{id}")
    public ResponseEntity<Map<String, String>> isExists(@PathVariable long id) {
        return ResponseEntity.ok(Map.of("message", String.valueOf(testTypeService.isExists(id))));
    }
}
