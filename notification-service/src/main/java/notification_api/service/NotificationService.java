package notification_api.service;

import lombok.RequiredArgsConstructor;
import notification_api.entity.NotificationTask;
import notification_api.entity.TaskStatus;
import notification_api.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;

    public void createNotificationForTrip(Long tripId) {

        NotificationTask task = new NotificationTask();
        task.setTripId(tripId);
        task.setRecipientType("SYSTEM");
        task.setMessage("Trip updated: " + tripId);
        task.setStatus(TaskStatus.PENDING);
        task.setAttempts(0);
        task.setCreatedAt(LocalDateTime.now());

        repository.save(task);
    }
}

