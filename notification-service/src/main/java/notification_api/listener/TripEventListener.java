package notification_api.listener;

import lombok.RequiredArgsConstructor;
import notification_api.dto.TripEvent;
import notification_api.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripEventListener {

    private final NotificationService notificationService;

    @RabbitListener(
            queues = "notification.queue",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void handleTripEvent(TripEvent event) {

        System.out.println("Trip event received: " + event.getTripId());

        notificationService.createNotification(event);
    }
}
