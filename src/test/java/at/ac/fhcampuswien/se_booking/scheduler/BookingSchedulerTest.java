package at.ac.fhcampuswien.se_booking.scheduler;

import at.ac.fhcampuswien.se_booking.AbstractMongoDBTest;
import at.ac.fhcampuswien.se_booking.dao.BookingItem;
import at.ac.fhcampuswien.se_booking.dao.BookingStatus;
import at.ac.fhcampuswien.se_booking.repository.BookingRepository;
import at.ac.fhcampuswien.se_booking.utils.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookingSchedulerTest extends AbstractMongoDBTest {

    @Autowired
    BookingScheduler bookingScheduler;
    @Autowired
    BookingRepository bookingRepository;

    @BeforeEach
    public void setUp() {
        insertTestData();
    }

    @Test
    void should_expire_open_bookings() {
        bookingScheduler.expireBookings();

        Optional<BookingItem> expired = bookingRepository.findById("booking1");

        Assertions.assertTrue(expired.isPresent());
        Assertions.assertEquals(BookingStatus.EXPIRED, expired.get().getBookingStatus());

        Optional<BookingItem> booked = bookingRepository.findById("booking2");

        Assertions.assertTrue(booked.isPresent());
        Assertions.assertEquals(BookingStatus.BOOKED, booked.get().getBookingStatus());
    }

    @Test
    void should_start_pending_bookings() {
        bookingScheduler.startBooking();

        Optional<BookingItem> pendingBooking = bookingRepository.findById("booking3");

        Assertions.assertTrue(pendingBooking.isPresent());
        Assertions.assertEquals(BookingStatus.PENDING, pendingBooking.get().getBookingStatus());

        Optional<BookingItem> booked = bookingRepository.findById("booking4");

        Assertions.assertTrue(booked.isPresent());
        Assertions.assertEquals(BookingStatus.BOOKED, booked.get().getBookingStatus());
    }

    private void insertTestData() {
        BookingItem bookingItem = new BookingItem();

        bookingItem.setId("booking1");
        bookingItem.setCreatedOn(LocalDateTime.now());
        bookingItem.setBookedFrom(Utils.getLocalDate(1, 1, 2022));
        bookingItem.setBookedUntil(Utils.getLocalDate(1, 2, 2022));
        bookingItem.setPrice(BigDecimal.valueOf(123F));
        bookingItem.setCurrency("USD");
        bookingItem.setPriceSaved(BigDecimal.valueOf(123F));
        bookingItem.setCurrency("USD");
        bookingItem.setCarId("car1");
        bookingItem.setUsername("user");
        bookingItem.setBookingStatus(BookingStatus.BOOKED);

        BookingItem bookingItem2 = new BookingItem();

        bookingItem2.setId("booking2");
        bookingItem2.setCreatedOn(LocalDateTime.now());
        bookingItem2.setBookedFrom(Utils.getLocalDate(1, 1, 2022));
        bookingItem2.setBookedUntil(Utils.getLocalDate(1, 2, 2122));
        bookingItem2.setPrice(BigDecimal.valueOf(123F));
        bookingItem2.setCurrency("USD");
        bookingItem2.setPriceSaved(BigDecimal.valueOf(123F));
        bookingItem2.setCurrency("USD");
        bookingItem2.setCarId("car2");
        bookingItem2.setUsername("user");
        bookingItem2.setBookingStatus(BookingStatus.BOOKED);

        BookingItem bookingItem3 = new BookingItem();

        bookingItem3.setId("booking3");
        bookingItem3.setCreatedOn(LocalDateTime.now());
        bookingItem3.setBookedFrom(LocalDate.now().plusDays(4));
        bookingItem3.setBookedUntil(LocalDate.now().plusDays(5));
        bookingItem3.setPrice(BigDecimal.valueOf(123F));
        bookingItem3.setCurrency("USD");
        bookingItem3.setPriceSaved(BigDecimal.valueOf(123F));
        bookingItem3.setCurrency("USD");
        bookingItem3.setCarId("car2");
        bookingItem3.setUsername("user");
        bookingItem3.setBookingStatus(BookingStatus.PENDING);

        BookingItem bookingItem4 = new BookingItem();

        bookingItem4.setId("booking4");
        bookingItem4.setCreatedOn(LocalDateTime.now());
        bookingItem4.setBookedFrom(LocalDate.now());
        bookingItem4.setBookedUntil(LocalDate.now().plusDays(3));
        bookingItem4.setPrice(BigDecimal.valueOf(123F));
        bookingItem4.setCurrency("USD");
        bookingItem4.setPriceSaved(BigDecimal.valueOf(123F));
        bookingItem4.setCurrency("USD");
        bookingItem4.setCarId("car2");
        bookingItem4.setUsername("user");
        bookingItem4.setBookingStatus(BookingStatus.PENDING);

        bookingRepository.saveAll(Arrays.asList(bookingItem, bookingItem2, bookingItem3, bookingItem4));
    }

}
