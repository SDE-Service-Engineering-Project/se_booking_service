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
    @JsonProperty("bookingId")
    String id;
    @JsonProperty("carId")
    String carId;
    @JsonProperty("bookedFrom")
    Long startDate;
    @JsonProperty("bookedUntil")
    Long endDate;
    @JsonProperty("action")
    String action;
}
