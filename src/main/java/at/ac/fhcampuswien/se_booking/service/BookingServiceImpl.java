package at.ac.fhcampuswien.se_booking.service;

import at.ac.fhcampuswien.se_booking.client.CarServiceClient;
import at.ac.fhcampuswien.se_booking.dao.BookingItem;
import at.ac.fhcampuswien.se_booking.dao.BookingStatus;
import at.ac.fhcampuswien.se_booking.dto.BookingDTO;
import at.ac.fhcampuswien.se_booking.dto.CarDTO;
import at.ac.fhcampuswien.se_booking.dto.CreateBookingDTO;
import at.ac.fhcampuswien.se_booking.dto.CreateBookingResponseDTO;
import at.ac.fhcampuswien.se_booking.mapper.BookingMapper;
import at.ac.fhcampuswien.se_booking.repository.BookingRepository;
import at.ac.fhcampuswien.se_booking.utils.LocalDateUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingServiceImpl implements BookingService {
    @Value("${currency-converter.default-currency}")
    String defaultCurrency;
    final BookingRepository bookingRepository;
    final BookingMapper bookingMapper;
    final CarServiceClient carServiceClient;

    @Override
    @Transactional(readOnly = true)
    public List<BookingDTO> getMyBookings(Long userId) {
        return bookingRepository.findAllByUserIdEquals(userId)
                .stream().map(item -> bookingMapper.toDto(item, carServiceClient.getCarById(item.getCarId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDTO getBookingById(String bookingId) {
        BookingItem bookingEntity = getBookingEntity(bookingId);
        CarDTO car = carServiceClient.getCarById(bookingEntity.getCarId());
        // TODO: Check if Authorized
//        checkIfAuthorized(bookingEntity);
        return bookingMapper.toDto(bookingEntity, car);
    }

    @Override
    @Transactional
    public CreateBookingResponseDTO createBooking(CreateBookingDTO createBookingDTO) {
        // TODO: Check if Car with the Id exists (on car_rental_service)

        CarDTO car = carServiceClient.getCarById(createBookingDTO.carId());
        assertCarIsNotBooked(createBookingDTO);

        // Booking Procedure
        BookingItem entity = bookingMapper.toItem(
                createBookingDTO,
                createBookingDTO.userId(),
                BigDecimal.valueOf(car.price() * createBookingDTO.daysToRent()).setScale(2, RoundingMode.HALF_UP),
                defaultCurrency,
                // TODO Calculate Saved Pricing - gRPC! For now we save "null"
                null,
                createBookingDTO.currency(),
                createBookingDTO.bookedFrom().isAfter(LocalDate.now()) ? BookingStatus.PENDING : BookingStatus.BOOKED
        );

        return bookingMapper.toCreateBookingResponseDto(bookingRepository.save(entity));
    }

    @Override
    public void expireBooking(String bookingId) {
        BookingItem bookingItem = getBookingEntity(bookingId);

        // TODO: Check if Authorized
//        checkIfAuthorized(bookingItem);

        if (bookingItem.getBookingStatus().equals(BookingStatus.PENDING)) {
            bookingRepository.delete(bookingItem);
            return;
        }

        bookingItem.setBookedUntil(LocalDate.now());
        bookingItem.setBookingStatus(BookingStatus.EXPIRED);

        bookingRepository.save(bookingItem);
    }

    private BookingItem getBookingEntity(String bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find booking with id " + bookingId)
        );
    }


    private void assertCarIsNotBooked(CreateBookingDTO createBookingDTO) {
        // Check if Car is already booked before proceeding
        List<BookingItem> savedBookingEntities = bookingRepository.findAllByCarIdEqualsAndBookingStatusIn(createBookingDTO.carId(), List.of(BookingStatus.BOOKED, BookingStatus.PENDING));
        if (savedBookingEntities.isEmpty()) return;
        if (savedBookingEntities
                .stream()
                .anyMatch(item -> LocalDateUtils.isOverlapping(
                        createBookingDTO.bookedFrom(),
                        createBookingDTO.bookedUntil(),
                        item.getBookedFrom(),
                        item.getBookedUntil()
                ))) {
            log.error("Car with the id {} is already booked in that timestamp!", createBookingDTO.carId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Car with the id " + createBookingDTO.carId() + " is already booked in the timespan!");
        }
    }
}
