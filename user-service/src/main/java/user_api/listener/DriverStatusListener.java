package user_api.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import user_api.dto.DriverStatusEvent;
import user_api.service.DriverService;

@Component
@RequiredArgsConstructor
@Slf4j
public class DriverStatusListener {

    private final DriverService driverService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String DRIVER_STATUS_KEY = "driver:status:";

    @RabbitListener(
            queues = "driver.status.queue",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void handleDriverStatusUpdate(DriverStatusEvent event) {

        log.info("Received driver status event: driverId={}, status={}",
                event.getDriverId(),
                event.getStatus());

        try {
            driverService.updateStatus(
                    event.getDriverId(),
                    event.getStatus()
            );

            redisTemplate.opsForValue().set(
                    DRIVER_STATUS_KEY + event.getDriverId(),
                    event.getStatus().name()
            );

            log.info("Driver {} status updated to {}",
                    event.getDriverId(),
                    event.getStatus());

        } catch (Exception e) {

            log.error("❌ Failed to update driver {}: {}",
                    event.getDriverId(),
                    e.getMessage());

            throw e;
        }
    }
}
