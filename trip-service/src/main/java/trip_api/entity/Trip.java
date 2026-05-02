package trip_api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "trips")
@Data
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long passengerId;
    private Long driverId;

    @Enumerated(EnumType.STRING)
    private TripStatus status;

    private String origin;
    private String destination;

    private Double price;

    private Integer rating;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

