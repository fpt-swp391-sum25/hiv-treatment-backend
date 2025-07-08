package backend.regimen.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import backend.regimen.dto.CreateRegimenRequest;
import backend.regimen.dto.UpdateRegimenRequest;
import backend.regimen.model.Regimen;
import backend.regimen.service.ReginmenService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/regimen")
@RequiredArgsConstructor
public class RegimenController {
    private final ReginmenService reginmenService;

    @PostMapping()
    public ResponseEntity<Map<String, String>> create(@RequestBody CreateRegimenRequest request) {
        return ResponseEntity.ok(Map.of("message", reginmenService.create(request)));
    }

    @GetMapping()
    public ResponseEntity<List<Regimen>> list() {
        return ResponseEntity.ok(reginmenService.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Regimen> get(@PathVariable long id) {
        return ResponseEntity.ok(reginmenService.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(@PathVariable long id, @RequestBody UpdateRegimenRequest request) {
        return ResponseEntity.ok(Map.of("message", reginmenService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable long id) {
        try {
            String message = reginmenService.delete(id);
            return ResponseEntity.ok(Map.of("message", message));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage())); 
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("message", e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi không xác định khi xóa phác đồ."));
        }
    }


    @GetMapping("doctor-id/{doctorId}")
    public ResponseEntity<List<Regimen>> getByDoctorId(@PathVariable long doctorId) {
        return ResponseEntity.ok(reginmenService.getByDoctorId(doctorId));
    }
}
