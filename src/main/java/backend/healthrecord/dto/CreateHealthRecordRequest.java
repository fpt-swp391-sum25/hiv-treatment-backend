package backend.healthrecord.dto;

public record CreateHealthRecordRequest(
        String treatmentStatus,
        Long scheduleId,
        String paymentRef,
        Long paymentId) {
}
