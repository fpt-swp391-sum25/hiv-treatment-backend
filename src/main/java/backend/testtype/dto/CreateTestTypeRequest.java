package backend.testtype.dto;

public record CreateTestTypeRequest(
    String testTypeName,
    float testTypePrice
) {
}
