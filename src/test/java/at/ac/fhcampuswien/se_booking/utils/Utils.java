package at.ac.fhcampuswien.se_booking.utils;

import at.ac.fhcampuswien.se_booking.dao.BookingItem;
import at.ac.fhcampuswien.se_booking.dao.BookingStatus;
import at.ac.fhcampuswien.se_booking.dto.BookingDTO;
import at.ac.fhcampuswien.se_booking.dto.CarDTO;
import at.ac.fhcampuswien.se_booking.dto.CreateBookingDTO;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@UtilityClass
public class Utils {
    public CarDTO carDTO() {

        String carId = "car1";
        String brand = "Toyota";
        String model = "Model 1";
        Integer constructionYear = 2018;
        Float price = 123.0f;
        String currency = "USD";
        LocalDateTime createdOn = LocalDateTime.now();

        return new CarDTO(carId, brand, model, constructionYear, price, currency, createdOn, null);
    }

    public BookingItem bookingItem() {
        BookingItem bookingItem = new BookingItem();

        bookingItem.setId("booking11");
        bookingItem.setCreatedOn(LocalDateTime.now());
        bookingItem.setBookedFrom(LocalDate.now());
        bookingItem.setBookedUntil(LocalDate.now().plusDays(5));
        bookingItem.setPrice(BigDecimal.valueOf(123F));
        bookingItem.setCurrency("USD");
        bookingItem.setPriceSaved(BigDecimal.valueOf(123F));
        bookingItem.setCurrency("USD");
        bookingItem.setCarId("car1");
        bookingItem.setUsername("user");
        bookingItem.setBookingStatus(BookingStatus.BOOKED);

        return bookingItem;
    }

    public BookingItem bookingItemPending() {
        BookingItem bookingItem = new BookingItem();

        bookingItem.setId("booking12");
        bookingItem.setCreatedOn(LocalDateTime.now());
        bookingItem.setBookedFrom(LocalDate.now().plusDays(6));
        bookingItem.setBookedUntil(LocalDate.now().plusDays(9));
        bookingItem.setPrice(BigDecimal.valueOf(123F));
        bookingItem.setCurrency("USD");
        bookingItem.setPriceSaved(BigDecimal.valueOf(123F));
        bookingItem.setCurrency("USD");
        bookingItem.setCarId("car1");
        bookingItem.setUsername("user");
        bookingItem.setBookingStatus(BookingStatus.PENDING);

        return bookingItem;
    }

    public BookingItem createdBookingItem() {
        BookingItem bookingItem = new BookingItem();

        bookingItem.setId("booking13");
        bookingItem.setCreatedOn(LocalDateTime.now());
        bookingItem.setBookedFrom(LocalDate.now().plusDays(10));
        bookingItem.setBookedUntil(LocalDate.now().plusDays(15));
        bookingItem.setPrice(BigDecimal.valueOf(123F));
        bookingItem.setCurrency("USD");
        bookingItem.setPriceSaved(BigDecimal.valueOf(123F));
        bookingItem.setCurrency("USD");
        bookingItem.setCarId("car1");
        bookingItem.setUsername("user");
        bookingItem.setBookingStatus(BookingStatus.PENDING);

        return bookingItem;
    }

    public BookingDTO bookingDTO() {
        String bookingId = "booking11";
        LocalDateTime createdOn = LocalDateTime.now();
        LocalDate bookedFrom = LocalDate.now();
        LocalDate bookedUntil = LocalDate.now().plusDays(5);
        BookingStatus bookingStatus = BookingStatus.BOOKED;
        Float price = 123.0f;
        String currency = "USD";
        Float priceSaved = 123.0f;
        String currencySaved = "USD";
        CarDTO car = carDTO();
        String username = "user";

        return new BookingDTO(bookingId, createdOn, bookedFrom, bookedUntil, bookingStatus.name(), price, currency, priceSaved, currencySaved, car, username);
    }

    public CreateBookingDTO createBookingDTOWithNoOverlap(String currency) {
        LocalDate bookedFrom = LocalDate.now().plusDays(10);
        LocalDate bookedUntil = LocalDate.now().plusDays(15);
        String carId = "car1";

        return new CreateBookingDTO(bookedFrom, bookedUntil, carId, currency);
    }

    public CreateBookingDTO createBookingDTOWithOverlap() {
        LocalDate bookedFrom = LocalDate.now().plusDays(7);
        LocalDate bookedUntil = LocalDate.now().plusDays(12);
        String carId = "car1";
        String currency = "USD";

        return new CreateBookingDTO(bookedFrom, bookedUntil, carId, currency);
    }

    public LocalDate getLocalDate(int day, int month) {
        return LocalDate.of(2023, month, day);
    }
    public LocalDate getLocalDate(int day, int month, int year) {
        return LocalDate.of(year, month, day);
    }
}
