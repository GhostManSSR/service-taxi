package trip_api.dto;

import lombok.Data;

import java.util.List;

@Data
public class OSRMResponse {
    public List<Route> routes;
    @Data public static class Route {
        public double distance, duration;
    }
}
