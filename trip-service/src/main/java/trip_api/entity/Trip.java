package trip_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "trips")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long passengerId;
    private Long driverId;

    @Enumerated(EnumType.STRING)
    private TripStatus status;

    private String origin;

    @Enumerated(EnumType.STRING)
    private Rates rate;

    @Column(name = "distance_km")
    private Double distanceKm;

    @Column(name = "duration_min")
    private Double durationMin;

    private String destination;

    private Double price;

    private Integer rating;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

