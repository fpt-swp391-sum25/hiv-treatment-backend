package backend.document.dto;

public record UpdateDocumentRequest(
    String title,
    
    String content
) {
}
