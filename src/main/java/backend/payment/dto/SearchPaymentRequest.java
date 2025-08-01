package backend.payment.dto;

public record SearchPaymentRequest (    
    String status, 

    String name,

    String description
) {
}