package at.ac.fhcampuswien.se_booking.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateBookingDTO(
        LocalDate bookedFrom,
        @NotNull(message = "No end date for booking set!")
        LocalDate bookedUntil,
        @NotNull(message = "No days to rent set!")
        Long daysToRent,
        @NotNull(message = "No car id provided!")
        String carId,
        String currency
) {
}
