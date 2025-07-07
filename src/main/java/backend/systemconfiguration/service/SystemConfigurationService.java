package backend.systemconfiguration.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import backend.systemconfiguration.dto.CreateSystemConfigurationRequest;
import backend.systemconfiguration.dto.UpdateSystemConfigurationRequest;
import backend.systemconfiguration.model.SystemConfiguration;
import backend.systemconfiguration.repository.SystemConfigurationRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SystemConfigurationService {
    @Autowired
    SystemConfigurationRepository systemConfigurationRepository;

    // Create system configuration
    public String create(CreateSystemConfigurationRequest request) {
        var systemConfiguration = SystemConfiguration.builder()
            .name(request.name())
            .value(request.value())
            .build();
        systemConfigurationRepository.save(systemConfiguration);

        return "SYSTEM CONFIGURATION CREATED SUCCESSFULLY WITH ID: " + systemConfiguration.getId();
    }

    // List system configurations
    public List<SystemConfiguration> list() {
        return systemConfigurationRepository.findAll();
    }

    // Read system configuration detail
    public SystemConfiguration get(Long id) {
        return systemConfigurationRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NO SYSTEM CONFIGURATION FOUND WITH ID: " + id));
    }

   // Update system configuration
    public String update(long id, UpdateSystemConfigurationRequest request) {        
        SystemConfiguration systemConfiguration = systemConfigurationRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NO SYSTEM CONFIGURATION FOUND WITH ID" + id));

        Optional.ofNullable(request.name()).ifPresent(systemConfiguration::setName);
        Optional.ofNullable(request.value()).ifPresent(systemConfiguration::setValue);
        systemConfigurationRepository.save(systemConfiguration);
        
        return "SYSTEM CONFIGURATION UPDATED SUCCESSFULLY WITH ID: " + id;
    }   
 
    // Delete system configuration
    public String delete(long id) {
        systemConfigurationRepository.delete(systemConfigurationRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NO SYSTEM CONFIGURATION FOUND WITH ID: " + id)));
        
        return "SYSTEM CONFIGURATION DELETED SUCCESSFULLY WITH ID: " + id;
    }
}
