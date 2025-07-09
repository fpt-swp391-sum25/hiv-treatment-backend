package backend.notification.model;

import java.time.LocalDateTime;

import backend.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String title;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String message;

    private boolean isRead;

    private LocalDateTime createdAt;
    
    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User user;
}
