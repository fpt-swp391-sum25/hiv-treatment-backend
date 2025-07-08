package backend.documentimage.dto;

public record CreateDocumentImageRequest (
    String image,

    long documentId
) {   
}