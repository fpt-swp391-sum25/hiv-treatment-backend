package backend.payment.dto;

import java.time.LocalDateTime;

public record CreatePaymentRequest (
    Boolean status, 

    String description, 

    LocalDateTime time,

    Float amount,
    
    long scheduleId
) {
}
