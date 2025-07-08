package backend.regimen.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import backend.healthrecord.repository.HealthRecordRepository;
import backend.regimen.dto.CreateRegimenRequest;
import backend.regimen.dto.UpdateRegimenRequest;
import backend.regimen.model.Regimen;
import backend.regimen.repository.RegimenRepository;
import backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReginmenService {

    @Autowired
    private final RegimenRepository regimenRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final HealthRecordRepository healthRecordRepository; 

    // Create regimen
    public String create(CreateRegimenRequest request) {
        var regimen = Regimen.builder()
            .regimenName(request.regimenName())
            .components(request.components())
            .description(request.description())
            .indications(request.indications())
            .contraindications(request.contraindications())
            .doctor(userRepository.findById(request.doctorId()).orElse(null))
            .build();

        regimenRepository.save(regimen);
        return "REGIMEN CREATED SUCCESSFULLY WITH ID: " + regimen.getId();
    }

    // List all regimens
    public List<Regimen> list() {
        return regimenRepository.findAll();
    }

    // Read regimen detail
    public Regimen get(long id) {
        return regimenRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NO REGIMEN FOUND WITH ID: " + id));
    }

    // Update regimen
    public String update(long id, UpdateRegimenRequest request) {
        Regimen regimen = regimenRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NO REGIMEN FOUND WITH ID: " + id));

        Optional.ofNullable(request.regimenName()).ifPresent(regimen::setRegimenName);
        Optional.ofNullable(request.components()).ifPresent(regimen::setComponents);
        Optional.ofNullable(request.description()).ifPresent(regimen::setDescription);
        Optional.ofNullable(request.indications()).ifPresent(regimen::setIndications);
        Optional.ofNullable(request.contraindications()).ifPresent(regimen::setContraindications);

        regimenRepository.save(regimen);
        return "REGIMEN UPDATED SUCCESSFULLY WITH ID: " + id;
    }

    // Check if regimen was used
    private boolean isRegimenInUse(Long regimenId) {
        return healthRecordRepository.existsByRegimenId(regimenId); 
    }

    // Delete regimen
    public String delete(long id) {
        Regimen regimen = regimenRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NO REGIMEN FOUND WITH ID: " + id));

        if (isRegimenInUse(id)) {
            throw new IllegalStateException("Phác đồ đang được sử dụng bởi bệnh nhân.");
        }

        regimenRepository.delete(regimen);
        return "REGIMEN DELETED SUCCESSFULLY WITH ID: " + id;
    }

    // List regimens by doctor ID or all if null
    public List<Regimen> getByDoctorId(long doctorId) {
        return regimenRepository.findByDoctorIdOrAll(doctorId);
    }
}
