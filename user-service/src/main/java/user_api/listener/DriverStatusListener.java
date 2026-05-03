package user_api.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import user_api.dto.DriverStatusEvent;
import user_api.service.DriverService;

@Component
@RequiredArgsConstructor
@Slf4j
public class DriverStatusListener {

    private final DriverService driverService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(
            queues = "driver.status.queue",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void handleDriverStatusUpdate(DriverStatusEvent event) {

        log.info("Received event: driverId={}, status={}",
                event.getDriverId(), event.getStatus());

        try {
            driverService.updateStatus(event.getDriverId(), event.getStatus());

            log.info("Driver {} status updated to {}",
                    event.getDriverId(), event.getStatus());

            rabbitTemplate.convertAndSend(
                    "user.exchange",
                    "driver.status.updated",
                    event
            );

        } catch (Exception e) {
            log.error("Failed to update driver {}: {}",
                    event.getDriverId(), e.getMessage());

            throw e;
        }
    }
}
