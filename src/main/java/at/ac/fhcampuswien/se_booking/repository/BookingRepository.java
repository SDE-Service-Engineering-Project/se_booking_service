package at.ac.fhcampuswien.se_booking.repository;

import at.ac.fhcampuswien.se_booking.dao.BookingItem;
import at.ac.fhcampuswien.se_booking.dao.BookingStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BookingRepository extends MongoRepository<BookingItem, String> {
    List<BookingItem> findAllByUsername(String username);
    List<BookingItem> findAllByCarIdEqualsAndBookingStatusIn(String carId, List<BookingStatus> bookingStatus);
}
