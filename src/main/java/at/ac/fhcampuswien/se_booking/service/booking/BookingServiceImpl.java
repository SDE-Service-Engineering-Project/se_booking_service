package at.ac.fhcampuswien.se_booking.service.booking;

import at.ac.fhcampuswien.se_booking.client.CarServiceClient;
import at.ac.fhcampuswien.se_booking.dao.BookingItem;
import at.ac.fhcampuswien.se_booking.dao.BookingStatus;
import at.ac.fhcampuswien.se_booking.dto.BookingDTO;
import at.ac.fhcampuswien.se_booking.dto.CarDTO;
import at.ac.fhcampuswien.se_booking.dto.CreateBookingDTO;
import at.ac.fhcampuswien.se_booking.dto.CreateBookingResponseDTO;
import at.ac.fhcampuswien.se_booking.kafka.BookingAction;
import at.ac.fhcampuswien.se_booking.kafka.BookingMessage;
import at.ac.fhcampuswien.se_booking.mapper.BookingMapper;
import at.ac.fhcampuswien.se_booking.repository.BookingRepository;
import at.ac.fhcampuswien.se_booking.service.currency.CurrencyConverterService;
import at.ac.fhcampuswien.se_booking.utils.LocalDateUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
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
    final KafkaTemplate<String, BookingMessage> kafkaTemplate;
    final CurrencyConverterService currencyConverterService;

    @Override
    @Transactional(readOnly = true)
    public List<BookingDTO> getMyBookings(String username) {
        return bookingRepository.findAllByUsername(username)
                .stream().map(item -> bookingMapper.toDto(item, carServiceClient.getCarById(item.getCarId()), username))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDTO getBookingById(String bookingId, String username) {
        BookingItem bookingEntity = getBookingEntity(bookingId);
        CarDTO car = carServiceClient.getCarById(bookingEntity.getCarId());

        checkIfAuthorized(bookingEntity, username);

        return bookingMapper.toDto(bookingEntity, car, username);
    }

    @Override
    @Transactional
    public CreateBookingResponseDTO createBooking(CreateBookingDTO createBookingDTO, String username) {
        CarDTO car = carServiceClient.getCarById(createBookingDTO.carId());
        assertCarIsNotBooked(createBookingDTO);

        // Booking Procedure
        BookingItem entity = bookingMapper.toItem(
                createBookingDTO,
                username,
                BigDecimal.valueOf(
                        car.price() * LocalDateUtils.calculateDaysBetween(Objects.requireNonNullElse(createBookingDTO.bookedFrom(), LocalDate.now()), createBookingDTO.bookedUntil())
                ).setScale(2, RoundingMode.HALF_UP),
                defaultCurrency,
                calculateSavedPricing(createBookingDTO, car.price()),
                createBookingDTO.currency(),
                createBookingDTO.bookedFrom().isAfter(LocalDate.now()) ? BookingStatus.PENDING : BookingStatus.BOOKED
        );

        BookingItem savedItem = bookingRepository.save(entity);
        sendKafkaMessage(
                savedItem.getId(),
                savedItem.getCarId(),
                LocalDateUtils.toEpochMillis(savedItem.getBookedFrom()),
                LocalDateUtils.toEpochMillis(savedItem.getBookedUntil()),
                BookingAction.CREATE
        );

        return bookingMapper.toCreateBookingResponseDto(savedItem);
    }

    @Override
    public void expireBooking(String bookingId, String username) {
        BookingItem bookingItem = getBookingEntity(bookingId);

        checkIfAuthorized(bookingItem, username);

        if (bookingItem.getBookingStatus().equals(BookingStatus.PENDING)) {
            bookingRepository.delete(bookingItem);
            sendKafkaMessage(
                    bookingItem.getId(),
                    bookingItem.getCarId(),
                    null,
                    null,
                    BookingAction.DELETE
            );
            return;
        }

        bookingItem.setBookedUntil(LocalDate.now());
        bookingItem.setBookingStatus(BookingStatus.EXPIRED);

        sendKafkaMessage(
                bookingItem.getId(),
                bookingItem.getCarId(),
                LocalDateUtils.toEpochMillis(bookingItem.getBookedFrom()),
                LocalDateUtils.toEpochMillis(bookingItem.getBookedUntil()),
                BookingAction.UPDATE
        );

        bookingRepository.save(bookingItem);
    }

    private void checkIfAuthorized(BookingItem bookingItem, String username) {
        if (!bookingItem.getUsername().equals(username)) {
            log.error("User {} is not authorized to change the booking with id {}", username, bookingItem.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not authorized to update this booking!");
        }
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

    private void sendKafkaMessage(String id, String carId, Long startDate, Long endDate, BookingAction action) {
        kafkaTemplate.sendDefault(
                BookingMessage.builder()
                        .id(id)
                        .carId(carId)
                        .startDate(startDate)
                        .endDate(endDate)
                        .action(action.toString())
                        .build()
        );
    }

    private BigDecimal calculateSavedPricing(CreateBookingDTO bookingDTO, float price) {
        if (StringUtils.hasText(bookingDTO.currency()) && !bookingDTO.currency().equals(defaultCurrency)) {
            return BigDecimal.valueOf(
                    currencyConverterService.convert(price, defaultCurrency, bookingDTO.currency()).amount() *
                            LocalDateUtils.calculateDaysBetween(Objects.requireNonNullElse(bookingDTO.bookedFrom(), LocalDate.now()), bookingDTO.bookedUntil())
            );
        }

        return null;
    }
}
