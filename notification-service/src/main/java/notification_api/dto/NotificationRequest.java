package notification_api.dto;

import lombok.Data;

@Data
public class NotificationRequest {
    private Long tripId;
    private String recipientType;
    private Long recipientId;
    private String message;
}
