package backend.testorder.dto;

import java.time.LocalDateTime;

public record UpdateTestOrderRequest(
    String name,

    String result,

    String unit,

    String note,
    
    String paymentStatus,

    LocalDateTime expectedResultTime,

    LocalDateTime actualResultTime,

    long testTypeId

) {
}
