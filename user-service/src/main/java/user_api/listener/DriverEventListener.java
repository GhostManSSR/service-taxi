package user_api.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import user_api.dto.DriverStatusEvent;
import user_api.service.DriverService;

@Service
@RequiredArgsConstructor
public class DriverEventListener {

    private final DriverService service;

    @RabbitListener(queues = "driver.status.queue")
    public void handleDriverStatus(DriverStatusEvent event) {

        service.updateStatus(event.getDriverId(), event.getStatus());
    }
}