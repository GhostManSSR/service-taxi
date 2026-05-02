package notification_api.listener;

import lombok.RequiredArgsConstructor;
import notification_api.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripEventListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = "notification.queue")
    public void handleTripEvent(Long tripId) {

        notificationService.createNotificationForTrip(tripId);
    }
}
