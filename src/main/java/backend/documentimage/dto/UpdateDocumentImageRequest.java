package backend.documentimage.dto;

public record UpdateDocumentImageRequest (
    String image,

    long documentId
) {   
}