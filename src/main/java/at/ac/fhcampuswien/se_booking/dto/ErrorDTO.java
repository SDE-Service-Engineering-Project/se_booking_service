package at.ac.fhcampuswien.se_booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

public record ErrorDTO(String message,
                       @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC")
                       Instant timestamp,
                       String path) {
}
