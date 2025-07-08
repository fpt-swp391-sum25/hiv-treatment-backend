package backend.regimen.dto;

public record  UpdateRegimenRequest(
    String regimenName,

    String components,

    String description,

    String indications,

    String contraindications,
    
    long doctorId
) {
}
