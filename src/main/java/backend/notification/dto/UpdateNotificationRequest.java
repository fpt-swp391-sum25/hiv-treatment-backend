package backend.notification.dto;

import java.time.LocalDateTime;

public record  UpdateNotificationRequest(
    String title,

    String message,
    
    boolean isRead,

    LocalDateTime createdAt
) {
}
