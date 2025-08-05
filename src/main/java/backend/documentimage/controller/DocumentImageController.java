package backend.documentimage.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.documentimage.dto.CreateDocumentImageRequest;
import backend.documentimage.dto.UpdateDocumentImageRequest;
import backend.documentimage.model.DocumentImage;
import backend.documentimage.service.DocumentImageService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/document-image")
@RequiredArgsConstructor
public class DocumentImageController {

    private final DocumentImageService documentImageService;

    @PostMapping
    public ResponseEntity<Map<String, String>> create(@RequestBody CreateDocumentImageRequest request) {
        return ResponseEntity.ok(Map.of("message", documentImageService.create(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentImage> get(@PathVariable long id) {
        return ResponseEntity.ok(documentImageService.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(@PathVariable long id, @RequestBody UpdateDocumentImageRequest request) {
        return ResponseEntity.ok(Map.of("message", documentImageService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable long id) {
        return ResponseEntity.ok(Map.of("message", documentImageService.delete(id)));
    }

    @GetMapping("/document-id/{documentId}")
    public ResponseEntity<List<DocumentImage>> getByDocumentId(@PathVariable long documentId) {
        return ResponseEntity.ok(documentImageService.getByDocumentId(documentId));
    }
}
