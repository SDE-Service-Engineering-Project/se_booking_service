package at.ac.fhcampuswien.se_booking.dto;

import java.time.LocalDateTime;

public record CarDTO(
        String carId,
        String brand,
        String model,
        Integer constructionYear,
        Float price,
        String currency,
        LocalDateTime createdOn,
        LocalDateTime modifiedOn) {
}
