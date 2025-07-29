package backend.testtype.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.testtype.model.TestType;

@Repository
public interface TestTypeRepository extends JpaRepository<TestType, Long> {
}