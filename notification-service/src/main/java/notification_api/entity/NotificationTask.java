package notification_api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_tasks")
@Data
public class NotificationTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long tripId;

    private String recipientType;

    private Long recipientId;

    private String message;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private Integer attempts;

    private LocalDateTime createdAt;
}
