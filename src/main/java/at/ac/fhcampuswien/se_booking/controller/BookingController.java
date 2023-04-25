package at.ac.fhcampuswien.se_booking.controller;

import at.ac.fhcampuswien.se_booking.client.CarServiceClient;
import at.ac.fhcampuswien.se_booking.dto.BookingDTO;
import at.ac.fhcampuswien.se_booking.dto.CarDTO;
import at.ac.fhcampuswien.se_booking.dto.CreateBookingDTO;
import at.ac.fhcampuswien.se_booking.dto.CreateBookingResponseDTO;
import at.ac.fhcampuswien.se_booking.service.BookingService;
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
    CarServiceClient carServiceClient;
    @Operation(summary = "Get all Bookings associated with the user")
    @GetMapping
    public ResponseEntity<List<BookingDTO>> getMyBookings(@RequestParam Long userId) {
        return ResponseEntity.ok(bookingService.getMyBookings(userId));
    }

    @Operation(summary = "Get a Booking by Id")
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable String bookingId) {
        return ResponseEntity.ok(bookingService.getBookingById(bookingId));
    }

    @Operation(summary = "Create a Booking")
    @PostMapping
    public ResponseEntity<CreateBookingResponseDTO> createBooking(@Valid @RequestBody CreateBookingDTO createBookingDTO) {
        LocalDateUtils.validateTimespan(Objects.requireNonNullElse(createBookingDTO.bookedFrom(), LocalDate.now()), createBookingDTO.bookedUntil());
        return new ResponseEntity<>(bookingService.createBooking(createBookingDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Expire a Booking")
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Void> expireBooking(@PathVariable String bookingId) {
        bookingService.expireBooking(bookingId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/test")
    public ResponseEntity<CarDTO> getCarById() {
        return new ResponseEntity<>(carServiceClient.getCarById("643ad77b5dd2e99a5b2fd2f8"), HttpStatus.OK);
    }
}
