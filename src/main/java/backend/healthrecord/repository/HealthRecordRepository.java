package backend.healthrecord.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend.healthrecord.model.HealthRecord;

@Repository
public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {
    @Query("SELECT h FROM HealthRecord h WHERE h.schedule.id = :scheduleId")
    HealthRecord findByScheduleId(@Param("scheduleId") long scheduleId);
}