package backend.testtype.dto;

public record UpdateTestTypeRequest(
    String testTypeName,
    float testTypePrice
) {
}