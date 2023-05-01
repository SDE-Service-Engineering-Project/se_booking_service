package at.ac.fhcampuswien.se_booking.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingMessage {
    @JsonProperty("_id")
    String id;
    @JsonProperty("carId")
    String carId;
    @JsonProperty("startDate")
    Long startDate;
    @JsonProperty("endDate")
    Long endDate;
    @JsonProperty("action")
    String action;
}
