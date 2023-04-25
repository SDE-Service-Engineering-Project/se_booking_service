package at.ac.fhcampuswien.se_booking.service;


import at.ac.fhcampuswien.se_booking.dto.BookingDTO;
import at.ac.fhcampuswien.se_booking.dto.CreateBookingDTO;
import at.ac.fhcampuswien.se_booking.dto.CreateBookingResponseDTO;

import java.util.List;

public interface BookingService {
    List<BookingDTO> getMyBookings(Long userId);
    BookingDTO getBookingById(String bookingId);
    CreateBookingResponseDTO createBooking(CreateBookingDTO createBookingDTO);
    void expireBooking(String bookingId);
}
