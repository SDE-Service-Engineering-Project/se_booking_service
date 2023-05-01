package at.ac.fhcampuswien.se_booking.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookingDTO(
        String bookingId,
        LocalDateTime createdOn,
        LocalDate bookedFrom,
        LocalDate bookedUntil,
        String bookingStatus,
        Float price,
        String currency,
        Float priceSaved,
        String currencySaved,
        CarDTO car,
        String username
) {
}
