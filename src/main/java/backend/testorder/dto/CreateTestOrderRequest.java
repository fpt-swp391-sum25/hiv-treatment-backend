package backend.testorder.dto;

import java.time.LocalDateTime;

public record CreateTestOrderRequest(
    String name,

    String note,

    LocalDateTime expectedResultTime,
    
    long healthRecordId
) {
}
