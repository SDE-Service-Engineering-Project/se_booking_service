package at.ac.fhcampuswien.se_booking.model.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingMessage extends KafkaMessageBaseModel{
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
