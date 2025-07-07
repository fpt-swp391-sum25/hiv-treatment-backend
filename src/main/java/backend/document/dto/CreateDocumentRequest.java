package backend.document.dto;

import java.time.LocalDate;

public record CreateDocumentRequest(
    String title,

    String content,
    
    LocalDate createdAt,

    long doctorId
) {
}
