package at.ac.fhcampuswien.se_booking.dto.currency;

public record ConvertCarPriceDTO(
        Long carId,
        String toCurrency
) {
}
