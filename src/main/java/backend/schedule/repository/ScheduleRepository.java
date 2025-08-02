package backend.schedule.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend.schedule.model.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
        List<Schedule> findByPatientId(long userId);

        List<Schedule> findByDoctorId(long userId);

        List<Schedule> findByType(String type);

        List<Schedule> findByStatus(String status);

        List<Schedule> findByDate(LocalDate date);

        List<Schedule> findBySlot(LocalTime slot);

        @Query("SELECT s.slot, COUNT(slot) as count FROM Schedule s " +
                        "WHERE s.doctor.id = :doctorId AND s.date = :date " +
                        "GROUP BY s.slot")
        List<Object[]> findSlotCountsByDoctorAndDate(@Param("doctorId") Long doctorId,
                        @Param("date") LocalDate date);

        @Query("SELECT s FROM Schedule s WHERE s.doctor.id = :doctorId AND s.date = :date AND s.status = :status")
        List<Schedule> findAvailableSchedulesByDoctorAndDate(
                        @Param("doctorId") Long doctorId,
                        @Param("date") LocalDate date,
                        @Param("status") String status);

        @Query("SELECT s FROM Schedule s WHERE s.date = :date AND s.status = :status AND s.patient IS NULL")
        List<Schedule> findActiveSchedulesByDate(LocalDate date, String status);

        @Query("SELECT s FROM Schedule s WHERE LOWER(s.patient.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
        List<Schedule> findByPatientFullName(@Param("name") String name);

        boolean existsByPatientIdAndDateAndStatus(Long patientId, LocalDate date, String status);
}
