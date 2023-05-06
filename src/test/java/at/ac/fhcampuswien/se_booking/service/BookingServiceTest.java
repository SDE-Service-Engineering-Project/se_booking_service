package at.ac.fhcampuswien.se_booking.service;

import at.ac.fhcampuswien.se_booking.client.CarServiceClient;
import at.ac.fhcampuswien.se_booking.dao.BookingItem;
import at.ac.fhcampuswien.se_booking.dao.BookingStatus;
import at.ac.fhcampuswien.se_booking.dto.BookingDTO;
import at.ac.fhcampuswien.se_booking.dto.CarDTO;
import at.ac.fhcampuswien.se_booking.dto.CreateBookingDTO;
import at.ac.fhcampuswien.se_booking.dto.currency.ConvertResultDTO;
import at.ac.fhcampuswien.se_booking.kafka.BookingMessage;
import at.ac.fhcampuswien.se_booking.mapper.BookingMapper;
import at.ac.fhcampuswien.se_booking.repository.BookingRepository;
import at.ac.fhcampuswien.se_booking.service.booking.BookingServiceImpl;
import at.ac.fhcampuswien.se_booking.service.currency_converter.CurrencyConverterService;
import at.ac.fhcampuswien.se_booking.utils.Utils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingServiceTest {
    @Mock
    BookingRepository bookingRepository;
    @Mock
    BookingMapper bookingMapper;
    @Mock
    CarServiceClient carServiceClient;
    @Mock
    KafkaTemplate<String, BookingMessage> kafkaTemplate;
    @Mock
    CurrencyConverterService currencyConverterService;

    @InjectMocks
    BookingServiceImpl bookingService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(bookingService, "defaultCurrency", "USD");
    }

    @Test
    void should_get_my_bookings() {
        BookingItem bookingItem = Utils.bookingItem();
        CarDTO carDTO = Utils.carDTO();

        Mockito.when(bookingRepository.findAllByUsername(bookingItem.getUsername()))
                .thenReturn(List.of(bookingItem));
        Mockito.when(bookingMapper.toDto(bookingItem, carDTO, bookingItem.getUsername()))
                .thenReturn(Utils.bookingDTO());
        Mockito.when(carServiceClient.getCarById(Mockito.any())).thenReturn(carDTO);

        List<BookingDTO> result = bookingService.getMyBookings(bookingItem.getUsername());

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(bookingItem.getId(), result.get(0).bookingId());
    }

    @Test
    void should_get_booking_by_id() {
        BookingItem bookingItem = Utils.bookingItem();
        CarDTO carDTO = Utils.carDTO();

        Mockito.when(bookingRepository.findById(bookingItem.getId()))
                .thenReturn(Optional.of(bookingItem));
        Mockito.when(carServiceClient.getCarById(Mockito.any())).thenReturn(carDTO);
        Mockito.when(bookingMapper.toDto(bookingItem, carDTO, bookingItem.getUsername()))
                .thenReturn(Utils.bookingDTO());

        BookingDTO result = bookingService.getBookingById(bookingItem.getId(), bookingItem.getUsername());

        Assertions.assertEquals(bookingItem.getId(), result.bookingId());
        Mockito.verify(bookingRepository, Mockito.times(1)).findById(bookingItem.getId());
    }

    @Test
    void should_throw_error_on_get_booking_by_id_from_other_user() {
        BookingItem bookingItem = Utils.bookingItem();
        CarDTO carDTO = Utils.carDTO();

        Mockito.when(bookingRepository.findById(bookingItem.getId()))
                .thenReturn(Optional.of(bookingItem));
        Mockito.when(carServiceClient.getCarById(Mockito.any())).thenReturn(carDTO);

        Assertions.assertThrows(
                ResponseStatusException.class, () -> bookingService.getBookingById(bookingItem.getId(), "user1")
        );
    }

    @Test
    void should_throw_error_on_wrong_booking_id() {
        String id = "wrongBookingId";

        Mockito.when(bookingRepository.findById(id))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(
                ResponseStatusException.class, () -> bookingService.getBookingById(id, "user")
        );
    }

    @Test
    void should_create_booking() {
        CarDTO carDTO = Utils.carDTO();
        CreateBookingDTO createBookingDTO = Utils.createBookingDTOWithNoOverlap("USD");

        Mockito.when(carServiceClient.getCarById(Mockito.any())).thenReturn(carDTO);
        Mockito.when(bookingRepository.findAllByCarIdEqualsAndBookingStatusIn(carDTO.carId(), List.of(BookingStatus.BOOKED, BookingStatus.PENDING)))
                .thenReturn(List.of(Utils.bookingItem(), Utils.bookingItemPending()));
        Mockito.when(bookingRepository.save(Mockito.any()))
                .thenReturn(Utils.bookingItem());

        bookingService.createBooking(createBookingDTO, "user");

        Mockito.verify(bookingRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(kafkaTemplate, Mockito.times(1)).sendDefault(Mockito.any());
    }

    @Test
    void should_throw_error_on_overlapping_bookings() {
        CarDTO carDTO = Utils.carDTO();
        CreateBookingDTO createBookingDTO = Utils.createBookingDTOWithOverlap();

        Mockito.when(carServiceClient.getCarById(Mockito.any())).thenReturn(carDTO);
        Mockito.when(bookingRepository.findAllByCarIdEqualsAndBookingStatusIn(carDTO.carId(), List.of(BookingStatus.BOOKED, BookingStatus.PENDING)))
                .thenReturn(List.of(Utils.bookingItem(), Utils.bookingItemPending()));

        Assertions.assertThrows(
                ResponseStatusException.class, () -> bookingService.createBooking(createBookingDTO, "user")
        );
    }

    @Test
    void should_create_booking_with_different_currency() {
        CarDTO carDTO = Utils.carDTO();
        CreateBookingDTO createBookingDTO = Utils.createBookingDTOWithNoOverlap("EUR");

        Mockito.when(carServiceClient.getCarById(Mockito.any())).thenReturn(carDTO);
        Mockito.when(bookingRepository.findAllByCarIdEqualsAndBookingStatusIn(carDTO.carId(), List.of(BookingStatus.BOOKED, BookingStatus.PENDING)))
                .thenReturn(List.of(Utils.bookingItem(), Utils.bookingItemPending()));
        Mockito.when(bookingRepository.save(Mockito.any()))
                .thenReturn(Utils.bookingItem());
        Mockito.when(currencyConverterService.convert(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new ConvertResultDTO(200f, "EUR"));

        bookingService.createBooking(createBookingDTO, "user");

        Mockito.verify(bookingRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(kafkaTemplate, Mockito.times(1)).sendDefault(Mockito.any());
    }

    @Test
    void should_expire_booking() {
        BookingItem bookingItem = Utils.bookingItem();
        Mockito.when(bookingRepository.findById(bookingItem.getId()))
                .thenReturn(Optional.of(bookingItem));

        bookingService.expireBooking(bookingItem.getId(), bookingItem.getUsername());

        Mockito.verify(kafkaTemplate, Mockito.times(1)).sendDefault(Mockito.any());
        Mockito.verify(bookingRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void should_delete_pending_booking() {
        BookingItem bookingItem = Utils.bookingItemPending();
        Mockito.when(bookingRepository.findById(bookingItem.getId()))
                .thenReturn(Optional.of(bookingItem));

        bookingService.expireBooking(bookingItem.getId(), bookingItem.getUsername());

        Mockito.verify(kafkaTemplate, Mockito.times(1)).sendDefault(Mockito.any());
        Mockito.verify(bookingRepository, Mockito.times(1)).delete(Mockito.any());
    }


}
