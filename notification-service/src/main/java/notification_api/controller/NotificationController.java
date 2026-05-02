package notification_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import notification_api.entity.NotificationTask;
import notification_api.repository.NotificationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "Управление уведомлениями")
public class NotificationController {

    private final NotificationRepository repository;

    @GetMapping("/{tripId}")
    @Operation(summary = "Вернуть уведомление о поездке")
    public List<NotificationTask> getByTrip(@PathVariable Long tripId) {
        return repository.findAll()
                .stream()
                .filter(n -> n.getTripId().equals(tripId))
                .toList();
    }
}
