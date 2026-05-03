package trip_api.dto;

import lombok.Data;

@Data
public class NominatimResult {
    public String lat, lon, display_name;
}
