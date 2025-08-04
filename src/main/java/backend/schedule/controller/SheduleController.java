    package backend.schedule.controller;

    import java.time.LocalDate;
    import java.time.LocalTime;
    import java.time.format.DateTimeFormatter;
    import java.util.List;
    import java.util.Map;

    import org.springframework.format.annotation.DateTimeFormat;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.DeleteMapping;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.PutMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RequestParam;
    import org.springframework.web.bind.annotation.RestController;

import backend.schedule.dto.CreateScheduleRequest;
import backend.schedule.dto.UpdateScheduleRequest;
import backend.schedule.model.Schedule;
import backend.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;

    @RestController
    @RequestMapping("/api/schedule")
    @RequiredArgsConstructor
    public class SheduleController {
        private final ScheduleService checkupScheduleService;

        @PostMapping()
        public ResponseEntity<Map<String, String>> create(@RequestBody CreateScheduleRequest request) {
            return ResponseEntity.ok(Map.of("message", checkupScheduleService.create(request)));
        }

    @GetMapping()
    public ResponseEntity<List<Schedule>> getSchedules(
            @RequestParam Long doctorId,
            @RequestParam String date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime slot) {
        LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        List<Schedule> schedules = checkupScheduleService.getSchedulesByDoctorDateAndSlot(
                doctorId, parsedDate, slot);
        System.out.println("<<<<<<<<<<<<"+ doctorId +","+ parsedDate +","+slot);
        return ResponseEntity.ok(schedules);
    }        @GetMapping("/list")
        public ResponseEntity<List<Schedule>> list() {
            return ResponseEntity.ok(checkupScheduleService.list());
        }

        @GetMapping("/available-slots")
        public ResponseEntity<List<String>> getAvailableSlots(@RequestParam Long doctorId,
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
            List<String> availableSlots = checkupScheduleService.getAvailableSlot(doctorId, date);

            return ResponseEntity.ok(availableSlots);
        }

        @GetMapping("/{id}")
        public ResponseEntity<Schedule> list(@PathVariable long id) {
            return ResponseEntity.ok(checkupScheduleService.get(id));
        }

        @PutMapping("/update/schedule-id/{id}")
        public ResponseEntity<Map<String, String>> update(@PathVariable long id,
                @RequestBody UpdateScheduleRequest request) {
            return ResponseEntity.ok(Map.of("message", checkupScheduleService.update(id, request)));
        }

        @PutMapping("/{id}/status")
        public ResponseEntity<Map<String, String>> updateStatus(@PathVariable long id,
                @RequestBody Map<String, String> request) {
            String status = request.get("status");
            checkupScheduleService.updateScheduleStatus(id, status);
            return ResponseEntity.ok(Map.of("message", "Schedule status updated successfully"));
        }

        @PutMapping("/register/schedule-id/{id}")
        public ResponseEntity<Map<String, String>> register(@PathVariable long id, @RequestParam int patientId,
                @RequestParam String type) {
            return ResponseEntity.ok(Map.of("message", checkupScheduleService.register(id, patientId, type)));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Map<String, String>> delete(@PathVariable long id) {
            return ResponseEntity.ok(Map.of("message", checkupScheduleService.delete(id)));
        }

        @DeleteMapping("/{scheduleId}/cancel")
        public ResponseEntity<String> cancelSchedule(
                @PathVariable Long scheduleId,
                @RequestParam Long patientId) {
            checkupScheduleService.cancelSchedule(scheduleId, patientId);
            return ResponseEntity.ok("Schedule cancelled successfully");
        }

        @GetMapping("/patient-id/{patientId}")
        public ResponseEntity<List<Schedule>> getByPatientId(@PathVariable long patientId) {
            return ResponseEntity.ok(checkupScheduleService.getByPatientId(patientId));
        }

        @GetMapping("/doctor-id/{doctorId}")
        public ResponseEntity<List<Schedule>> getByDoctorId(@PathVariable long doctorId) {
            return ResponseEntity.ok(checkupScheduleService.getByDoctorId(doctorId));
        }

        @GetMapping("/type/{type}")
        public ResponseEntity<List<Schedule>> getByType(@PathVariable String type) {
            return ResponseEntity.ok(checkupScheduleService.getByType(type));
        }

        @GetMapping("/status/{status}")
        public ResponseEntity<List<Schedule>> getByStatus(@PathVariable String status) {
            return ResponseEntity.ok(checkupScheduleService.getByStatus(status));
        }

        @GetMapping("/date/{date}")
        public ResponseEntity<List<Schedule>> getByDate(@PathVariable LocalDate date) {
            return ResponseEntity.ok(checkupScheduleService.getByDate(date));
        }

        @GetMapping("/available-slots/{date}")
        public ResponseEntity<List<Schedule>> getAvailableSlotByDate(@PathVariable LocalDate date) {
            return ResponseEntity.ok(checkupScheduleService.getAvailableSlotByDate(date));
        }

        @GetMapping("/slot/{slot}")
        public ResponseEntity<List<Schedule>> getBySlot(@PathVariable LocalTime slot) {
            return ResponseEntity.ok(checkupScheduleService.getBySlot(slot));
        }

        @PutMapping("/bulk-update")
        public ResponseEntity<Map<String, String>> bulkUpdateSchedules(@RequestBody BulkUpdateScheduleRequest request) {
            checkupScheduleService.bulkUpdateSchedules(request.getDoctorId(), request.getDate(), request.getRoomCode(), request.getSlot());
            return ResponseEntity.ok(Map.of("message", "Bulk update schedules successful"));
        }

        @DeleteMapping("/bulk-delete")
        public ResponseEntity<Map<String, String>> bulkDeleteSchedules(
                @RequestParam Long doctorId,
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
            checkupScheduleService.bulkDeleteSchedules(doctorId, date);
            return ResponseEntity.ok(Map.of("message", "Bulk delete schedules successful"));
        }

        @GetMapping("/search")
        public ResponseEntity<List<Schedule>> searchByPatientName(@RequestParam String name) {
            return ResponseEntity.ok(checkupScheduleService.searchByPatientName(name));
        }
    }