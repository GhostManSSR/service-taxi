package user_api.mapper;

import org.springframework.stereotype.Component;
import user_api.dto.DriverResponse;
import user_api.entity.Driver;

import java.util.ArrayList;
import java.util.List;

@Component
public class DriverMapper {

    public DriverResponse toDto(Driver driver) {
        return new DriverResponse(
                driver.getId(),
                driver.getName(),
                driver.getStatus()
        );
    }

    public List<DriverResponse> toDto(List<Driver> drivers) {
        return drivers.stream()
                .map(this::toDto)
                .toList();
    }
}
