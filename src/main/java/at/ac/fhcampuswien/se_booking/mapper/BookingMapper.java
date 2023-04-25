package at.ac.fhcampuswien.se_booking.mapper;

import at.ac.fhcampuswien.se_booking.dao.BookingItem;
import at.ac.fhcampuswien.se_booking.dao.BookingStatus;
import at.ac.fhcampuswien.se_booking.dto.BookingDTO;
import at.ac.fhcampuswien.se_booking.dto.CarDTO;
import at.ac.fhcampuswien.se_booking.dto.CreateBookingDTO;
import at.ac.fhcampuswien.se_booking.dto.CreateBookingResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Mapper(componentModel = "spring", imports = {LocalDate.class, Objects.class})
public abstract class BookingMapper {

    @Mapping(target = "car", source = "car")
    @Mapping(target = "bookingId", source = "bookingItem.id")
    @Mapping(target = "createdOn", source = "bookingItem.createdOn")
    @Mapping(target = "bookedFrom", source = "bookingItem.bookedFrom")
    @Mapping(target = "bookedUntil", source = "bookingItem.bookedUntil")
    @Mapping(target = "bookingStatus", source = "bookingItem.bookingStatus")
    @Mapping(target = "price", source = "bookingItem.price")
    @Mapping(target = "currency", source = "bookingItem.currency")
    @Mapping(target = "priceSaved", source = "bookingItem.priceSaved")
    @Mapping(target = "currencySaved", source = "bookingItem.currencySaved")
    @Mapping(target = "userId", source = "bookingItem.userId")
    public abstract BookingDTO toDto(BookingItem bookingItem, CarDTO car);

    @Mapping(target = "bookingStatus", source = "bookingStatus")
    @Mapping(target = "createdOn", expression = "java(LocalDateTime.now())")
    @Mapping(target = "bookedFrom", expression = "java(Objects.requireNonNullElse(dto.bookedFrom(), LocalDate.now()))")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "currency", source = "defaultCurrency")
    @Mapping(target = "priceSaved", source = "priceSaved")
    @Mapping(target = "currencySaved", expression = "java(Objects.requireNonNullElse(dto.currency(), defaultCurrency))")
    @Mapping(target = "userId", source = "userId")
    public abstract BookingItem toItem(CreateBookingDTO dto, Long userId, BigDecimal price, String defaultCurrency, BigDecimal priceSaved, String currencySaved, BookingStatus bookingStatus);

    @Mapping(target = "bookingId", source = "id")
    @Mapping(target = "bookedFrom", source = "bookedFrom")
    public abstract CreateBookingResponseDTO toCreateBookingResponseDto(BookingItem entity);
}
