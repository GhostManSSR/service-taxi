package notification_api.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import notification_api.dto.TripEvent;
import notification_api.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripEventListener {

    private final NotificationService notificationService;

    @RabbitListener(
            queues = "notification.queue",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void handleTripEvent(TripEvent event) {
        log.info("🔥 Received TripEvent from RabbitMQ:");
        log.info("tripId: {}", event.getTripId());
        log.info("passengerId: {}", event.getPassengerId());
        log.info("driverId: {}", event.getDriverId());
        log.info("route: {} → {}", event.getOrigin(), event.getDestination());
        log.info("price: {}", event.getPrice());
        log.info("distanceKm: {}", event.getDistanceKm());
        log.info("durationMin: {}", event.getDurationMin());
        log.info("status: {}", event.getStatus());

        notificationService.createNotification(event);
        log.info("✅ Notification created for tripId={}", event.getTripId());
    }
}
