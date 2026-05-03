package user_api.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import user_api.dto.DriverRatingEvent;
import user_api.service.DriverService;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverRatingListener {

    private final DriverService driverService;

    @RabbitListener(queues = "driver.rating.queue")
    public void handleRating(DriverRatingEvent event) {

        log.info("Driver {} got rating {}", event.getDriverId(), event.getRating());

        driverService.updateRating(
                event.getDriverId(),
                event.getRating()
        );
    }
}

