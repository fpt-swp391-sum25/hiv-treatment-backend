package backend.testtype.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import backend.testorder.model.TestOrder;
import backend.testorder.repository.TestOrderRepository;
import backend.testtype.dto.CreateTestTypeRequest;
import backend.testtype.dto.UpdateTestTypeRequest;
import backend.testtype.model.TestType;
import backend.testtype.repository.TestTypeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TestTypeService {

    @Autowired
    private final TestTypeRepository testTypeRepository;
    private final TestOrderRepository testOrderRepository;

    // Create test type
    public String create(CreateTestTypeRequest request) {
        var testType = TestType.builder()
                .testTypeName(request.testTypeName())
                .testTypePrice(request.testTypePrice())
                .build();

        testTypeRepository.save(testType);
        return "TEST TYPE CREATED SUCCESSFULLY WITH ID: " + testType.getId();
    }

    // List all test types
    public List<TestType> list() {
        return testTypeRepository.findAll();
    }

    public String isExists(long id) {
        List<TestOrder> testOrder = testOrderRepository.findByTestTypeId(id);
        if (!testOrder.isEmpty()) {
            return "TEST TYPE ALREADY IN USED";
        }
        return "TEST TYPE CAN BE DELETED";
    }

    // Update test type
    public String update(long id, UpdateTestTypeRequest request) {
        TestType testType = testTypeRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NO TEST TYPE FOUND WITH ID: " + id));

        Optional.ofNullable(request.testTypeName()).ifPresent(testType::setTestTypeName);
        Optional.ofNullable(request.testTypePrice()).ifPresent(testType::setTestTypePrice);
        testTypeRepository.save(testType);

        return "TEST TYPE UPDATED SUCCESSFULLY WITH ID: " + id;
    }

    // Delete test type
    public String delete(long id) {
        testTypeRepository.delete(testTypeRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NO TEST TYPE FOUND WITH ID: " + id)));

        return "TEST TYPE DELETED SUCCESSFULLY WITH ID: " + id;
    }
}
