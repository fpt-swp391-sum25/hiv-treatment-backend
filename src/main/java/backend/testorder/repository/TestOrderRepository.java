package backend.testorder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.testorder.model.TestOrder;

@Repository
public interface TestOrderRepository extends JpaRepository<TestOrder, Long> {
    List<TestOrder> findByHealthRecordId(long healthRecordId);

    List<TestOrder> findByTestTypeId(long testTypeId);
}
