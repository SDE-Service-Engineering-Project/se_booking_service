package at.ac.fhcampuswien.se_booking.controller;

import at.ac.fhcampuswien.se_booking.dto.BookingDTO;
import at.ac.fhcampuswien.se_booking.dto.CreateBookingDTO;
import at.ac.fhcampuswien.se_booking.dto.CreateBookingResponseDTO;
import at.ac.fhcampuswien.se_booking.service.booking.BookingService;
import at.ac.fhcampuswien.se_booking.utils.LocalDateUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Log4j2
public class BookingController {
    BookingService bookingService;
    @Operation(summary = "Get all Bookings associated with the user")
    @GetMapping("/")
    public ResponseEntity<List<BookingDTO>> getMyBookings(@RequestHeader("X-USERNAME") String username) {
        return ResponseEntity.ok(bookingService.getMyBookings(username));
    }

    @Operation(summary = "Get a Booking by Id")
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable String bookingId, @RequestHeader("X-USERNAME") String username) {
        return ResponseEntity.ok(bookingService.getBookingById(bookingId, username));
    }

    @Operation(summary = "Create a Booking")
    @PostMapping("/")
    public ResponseEntity<CreateBookingResponseDTO> createBooking(@Valid @RequestBody CreateBookingDTO createBookingDTO, @RequestHeader("X-USERNAME") String username) {
        LocalDateUtils.validateTimespan(Objects.requireNonNullElse(createBookingDTO.bookedFrom(), LocalDate.now()), createBookingDTO.bookedUntil());
        return new ResponseEntity<>(bookingService.createBooking(createBookingDTO, username), HttpStatus.CREATED);
    }

    @Operation(summary = "Expire a Booking")
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Void> expireBooking(@PathVariable String bookingId, @RequestHeader("X-USERNAME") String username) {
        bookingService.expireBooking(bookingId, username);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
