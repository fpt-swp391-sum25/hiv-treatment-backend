package backend.testorder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend.testorder.model.TestOrder;

@Repository
public interface TestOrderRepository extends JpaRepository<TestOrder, Long> {
    List<TestOrder> findByHealthRecordId(long healthRecordId);

    @Query("SELECT t.id FROM TestOrder t WHERE t.type.id = :testTypeId")
    List<TestOrder> findByTestTypeId(@Param("testTypeId") long testTypeId);
}
