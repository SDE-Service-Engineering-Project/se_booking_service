package at.ac.fhcampuswien.se_booking.service;


import at.ac.fhcampuswien.se_booking.dto.BookingDTO;
import at.ac.fhcampuswien.se_booking.dto.CreateBookingDTO;
import at.ac.fhcampuswien.se_booking.dto.CreateBookingResponseDTO;

import java.util.List;

public interface BookingService {
    List<BookingDTO> getMyBookings(String username);
    BookingDTO getBookingById(String bookingId, String username);
    CreateBookingResponseDTO createBooking(CreateBookingDTO createBookingDTO, String username);
    void expireBooking(String bookingId, String username);
}
