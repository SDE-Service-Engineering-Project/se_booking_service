package at.ac.fhcampuswien.se_booking.dto;

import java.time.LocalDate;

public record CreateBookingResponseDTO(
        String bookingId,
        LocalDate createdOn,
        LocalDate bookedFrom,
        LocalDate bookedUntil,
        String bookingStatus,
        Float price,
        String currency,
        Float priceSaved,
        String currencySaved,
        String carId,
        Long userId
) {
}
