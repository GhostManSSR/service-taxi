package notification_api.service;

import lombok.RequiredArgsConstructor;
import notification_api.dto.TripEvent;
import notification_api.entity.NotificationTask;
import notification_api.entity.TaskStatus;
import notification_api.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;

    public void createNotification(TripEvent event) {

        NotificationTask task = new NotificationTask();
        task.setTripId(event.getTripId());
        task.setRecipientType("PASSENGER");
        task.setRecipientId(event.getPassengerId());

        task.setMessage(
                "Trip " + event.getStatus() +
                        " | " + event.getOrigin() +
                        " → " + event.getDestination() +
                        " | ₽" + event.getPrice() +
                        " dist: " + event.getDistanceKm() +
                        " time: " + event.getDurationMin() +
                        " rating: " + event.getRating()
        );

        task.setStatus(TaskStatus.PENDING);
        task.setAttempts(0);
        task.setCreatedAt(LocalDateTime.now());

        repository.save(task);
    }
}

