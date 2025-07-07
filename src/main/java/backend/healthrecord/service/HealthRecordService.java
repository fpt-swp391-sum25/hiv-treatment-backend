package backend.healthrecord.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import backend.healthrecord.dto.CreateHealthRecordRequest;
import backend.healthrecord.dto.UpdateHealthRecordRequest;
import backend.healthrecord.model.HealthRecord;
import backend.healthrecord.repository.HealthRecordRepository;
import backend.regimen.repository.RegimenRepository;
import backend.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HealthRecordService {
    @Autowired
    private final HealthRecordRepository healthRecordRepository;
    private final ScheduleRepository scheduleRepository;
    private final RegimenRepository regimenRepository;

    // Create health record
    public String create(CreateHealthRecordRequest request) {
        var healthRecord = HealthRecord.builder()
                .treatmentStatus("Đang chờ khám")
                .schedule(scheduleRepository.findById(request.scheduleId()).get())
                .build();
        healthRecordRepository.save(healthRecord);

        return "CHECK-UP RECORD CREATED WITH ID: " + healthRecord.getId();
    }

    // List health records
    public List<HealthRecord> list() {
        return healthRecordRepository.findAll();
    }

    // Read health record detail
    public HealthRecord get(long id) {
        return healthRecordRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "NO HEALTH RECORD FOUND WITH ID: " + id));
    }

    // Update health record detail
    public String update(long id, UpdateHealthRecordRequest request) {
        HealthRecord record = healthRecordRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "NO HEALTH RECORD FOUND WITH ID: " + id));
        System.out.println(">>>>>>>>>>>>>>>" + request.regimenId());
        Optional.ofNullable(request.hivStatus()).ifPresent(record::setHivStatus);
        Optional.ofNullable(request.bloodType()).ifPresent(record::setBloodType);
        Optional.ofNullable(request.weight()).ifPresent(record::setWeight);
        Optional.ofNullable(request.height()).ifPresent(record::setHeight);
        Optional.ofNullable(request.treatmentStatus()).ifPresent(record::setTreatmentStatus);
        Optional.ofNullable(scheduleRepository.findById(request.scheduleId()).get()).ifPresent(record::setSchedule);
        if (request.regimenId() == null) {
            record.setRegimen(null);
        } else {
            regimenRepository.findById(request.regimenId())
                    .ifPresent(record::setRegimen);
        }
        healthRecordRepository.save(record);

        return "HEALTH RECORD UPDATED SUCCESSFULLTY WITH ID: " + record.getId();
    }

    // Delete health record
    public String delete(long id) {
        healthRecordRepository.delete(healthRecordRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "NO HEALTH RECORD FOUND WITH ID: " + id)));

        return "HEALTH RECORD DELETED SUCCESSFULLY WITH ID:" + id;
    }

    // Read health record by schedule ID
    public HealthRecord getByScheduleId(long scheduleId) {
        HealthRecord record = healthRecordRepository.findByScheduleId(scheduleId);

        return record;
    }
}
