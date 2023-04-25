package at.ac.fhcampuswien.se_booking.dao;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Document("bookings")
@Data
public class BookingItem {
    @Id
    String id;
    LocalDateTime createdOn;
    LocalDate bookedFrom;
    LocalDate bookedUntil;
    BookingStatus bookingStatus;
    BigDecimal price;
    String currency;
    BigDecimal priceSaved;
    String currencySaved;
    String carId;
    Long userId;
}
