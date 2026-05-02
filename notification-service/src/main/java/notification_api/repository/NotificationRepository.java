package notification_api.repository;

import notification_api.entity.NotificationTask;
import notification_api.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationTask, Long> {

    List<NotificationTask> findByStatus(TaskStatus status);
}
