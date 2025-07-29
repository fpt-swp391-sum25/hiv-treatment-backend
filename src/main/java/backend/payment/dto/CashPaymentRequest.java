package backend.payment.dto;

public record CashPaymentRequest (
    Long scheduleId,

    Integer amount
){
}