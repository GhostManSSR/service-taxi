package notification_api.worker;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import notification_api.entity.NotificationTask;
import notification_api.entity.TaskStatus;
import notification_api.repository.NotificationRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class NotificationWorker {

    private final NotificationRepository repository;

    private final ExecutorService executor =
            Executors.newFixedThreadPool(4);

    @PostConstruct
    public void start() {

        for (int i = 0; i < 4; i++) {
            executor.submit(this::process);
        }
    }

    private void process() {

        while (true) {

            List<NotificationTask> tasks =
                    repository.findByStatus(TaskStatus.PENDING);

            for (NotificationTask task : tasks) {

                if (!lock(task)) continue;

                try {
                    send(task);

                    task.setStatus(TaskStatus.SENT);
                } catch (Exception e) {

                    task.setAttempts(task.getAttempts() + 1);

                    if (task.getAttempts() >= 3) {
                        task.setStatus(TaskStatus.FAILED);
                    } else {
                        task.setStatus(TaskStatus.PENDING);
                    }
                }

                repository.save(task);
            }

            sleep();
        }
    }

    private boolean lock(NotificationTask task) {
        if (task.getStatus() != TaskStatus.PENDING) {
            return false;
        }

        task.setStatus(TaskStatus.PROCESSING);
        repository.save(task);
        return true;
    }

    private void send(NotificationTask task) {

        System.out.println("Sending notification: " + task.getMessage());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}
    }

    private void sleep() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {}
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
    }
}
