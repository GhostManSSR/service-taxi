package user_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import user_api.dto.CreatePassengerRequest;
import user_api.entity.Passenger;
import user_api.repository.PassengerRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PassengerService {

    private final PassengerRepository repository;

    public Passenger create(CreatePassengerRequest request) {
        Passenger p = new Passenger();
        p.setName(request.getName());
        p.setEmail(request.getEmail());
        p.setPhone(request.getPhone());
        p.setCreatedAt(LocalDateTime.now());

        return repository.save(p);
    }

    public Passenger get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Passenger not found"));
    }
}
