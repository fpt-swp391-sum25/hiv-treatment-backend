package backend.schedule.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import backend.healthrecord.model.HealthRecord;
import backend.healthrecord.repository.HealthRecordRepository;
import backend.payment.repository.PaymentRepository;
import backend.payment.service.PaymentService;
import backend.schedule.dto.CreateScheduleRequest;
import backend.schedule.dto.UpdateScheduleRequest;
import backend.schedule.model.Schedule;
import backend.schedule.repository.ScheduleRepository;
import backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final PaymentService paymentService;
    @Autowired
    private final ScheduleRepository scheduleRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PaymentRepository paymentRepository;

    @Autowired
    private final HealthRecordRepository healthRecordRepository;

    // Tạo ca khám bệnh
    public String create(CreateScheduleRequest request) {

        List<Object[]> slotCounts = scheduleRepository.findSlotCountsByDoctorAndDate(request.doctorId(),
                request.date());
        Map<LocalTime, Long> slotCountMap = slotCounts.stream()
                .collect(Collectors.toMap(
                        result -> (LocalTime) result[0],
                        result -> (Long) result[1]));

        if (slotCountMap.getOrDefault(request.slot(), 0L) >= 5) {
            throw new IllegalStateException("Slot " + request.slot() + " đã đủ 5 lịch hẹn.");
        }

        Schedule checkupSchedule = Schedule.builder()
                .roomCode(request.roomCode())
                .date(request.date())
                .slot(request.slot())
                .doctor(userRepository.findById(request.doctorId()).get())
                .status("Trống")
                .build();

        scheduleRepository.save(checkupSchedule);

        return "SLOT CREATED SUCCESSFULLY WITH ID: " + checkupSchedule.getId();
    }

    public List<Schedule> getSchedulesByDoctorDateAndStatus(Long doctorId, LocalDate date, String status) {
        return scheduleRepository.findAvailableSchedulesByDoctorAndDate(doctorId, date, "Tr\u1ed1ng")
                .stream()
                .filter(schedule -> status == null || schedule.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    // List schedule slots
    public List<Schedule> list() {
        return scheduleRepository.findAll();
    }

    // Read schedule slot detail
    public Schedule get(long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NO SLOT FOUND WITH ID: " + id));
    }

    // Read available slots
    public List<String> getAvailableSlot(Long doctorId, LocalDate date) {
        List<LocalTime> allSlots = Arrays.asList(
                LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30),
                LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(13, 0),
                LocalTime.of(13, 30), LocalTime.of(14, 0), LocalTime.of(14, 30), LocalTime.of(15, 0),
                LocalTime.of(15, 30), LocalTime.of(16, 0), LocalTime.of(16, 30));

        List<Object[]> slotCounts = scheduleRepository.findSlotCountsByDoctorAndDate(doctorId, date);

        Map<LocalTime, Long> slotCountMap = slotCounts.stream()
                .collect(Collectors.toMap(
                        result -> (LocalTime) result[0],
                        result -> (Long) result[1],
                        (v1, v2) -> v1));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        List<String> availableSlots = allSlots.stream()
                .filter(slot -> {
                    Long count = slotCountMap.getOrDefault(slot, 0L);
                    return count < 5;
                })
                .map(slot -> slot.format(formatter))
                .collect(Collectors.toList());

        return availableSlots;
    }

    // Update schedule slot
    public String update(long id, UpdateScheduleRequest request) {
        Schedule checkupSchedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NO SLOT FOUND WITH ID" + id));

        Optional.ofNullable(request.roomCode()).ifPresent(checkupSchedule::setRoomCode);
        Optional.ofNullable(request.date()).ifPresent(checkupSchedule::setDate);
        Optional.ofNullable(request.slot()).ifPresent(checkupSchedule::setSlot);
        Optional.ofNullable(request.status()).ifPresent(checkupSchedule::setStatus);
        Optional.ofNullable(userRepository.findById(request.doctorId()).get()).ifPresent(checkupSchedule::setDoctor);

        return "SLOT UPDATED SUCCESSFULLY WITH ID: " + id;
    }

    public void cancelSchedule(Long scheduleId, Long patientId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));

        if (!List.of("Đang chờ", "Đang chờ thanh toán", "Đã thanh toán").contains(schedule.getStatus())) {
            throw new IllegalStateException("Cannot cancel schedule with status: " + schedule.getStatus());
        }

        schedule.setStatus("Trống");
        schedule.setPatient(null);
        schedule.setType(null);
        scheduleRepository.save(schedule);

        HealthRecord record = healthRecordRepository.findByScheduleId(scheduleId);
        if (record != null) {
            healthRecordRepository.delete(record);
        }

        paymentRepository.deleteById(paymentRepository.findByScheduleId(scheduleId).get().getId());
    }

    // Delete schedule slot
    public String delete(long id) {
        scheduleRepository.delete(scheduleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NO SLOT FOUND WITH ID: " + id)));

        return "SLOT DELETED SUCCESSFULLY WITH ID: " + id;
    }

    // Register schedule slot by patient ID
    public String register(long id, long patientId, String type) {
        Schedule CheckupSchedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NO SLOT FOUND WITH ID: " + id));

        Optional.ofNullable(userRepository.findById(patientId).get()).ifPresent(CheckupSchedule::setPatient);
        Optional.ofNullable(type).ifPresent(CheckupSchedule::setType);
        CheckupSchedule.setStatus("Đang chờ thanh toán");
        scheduleRepository.save(CheckupSchedule);

        return "SLOT REGISTERED SUCCESSFULLY WITH ID: " + id;
    }

    // List schedule slots by patient ID
    public List<Schedule> getByPatientId(long patientId) {
        return scheduleRepository.findByPatientId(patientId);
    }

    // List schedule slots by doctor ID
    public List<Schedule> getByDoctorId(long doctorId) {
        return scheduleRepository.findByDoctorId(doctorId);
    }

    // List schedule slots by type
    public List<Schedule> getByType(String type) {
        return scheduleRepository.findByType(type);
    }

    // List schedule slots by status
    public List<Schedule> getByStatus(String status) {
        return scheduleRepository.findByStatus(status);
    }

    // List schedule slots by date
    public List<Schedule> getByDate(LocalDate date) {
        return scheduleRepository.findByDate(date);
    }

    public List<Schedule> getAvailableSlotByDate(LocalDate date) {
        return scheduleRepository.findActiveSchedulesByDate(date, "Tr\u1ed1ng");
    }

    // List schedule slots by slot
    public List<Schedule> getBySlot(LocalTime slot) {
        return scheduleRepository.findBySlot(slot);
    }
}