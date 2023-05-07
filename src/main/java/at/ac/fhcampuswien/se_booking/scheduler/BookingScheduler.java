package at.ac.fhcampuswien.se_booking.scheduler;

import at.ac.fhcampuswien.se_booking.dao.BookingItem;
import at.ac.fhcampuswien.se_booking.dao.BookingStatus;
import at.ac.fhcampuswien.se_booking.repository.BookingRepository;
import at.ac.fhcampuswien.se_booking.utils.LocalDateUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@Log4j2
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BookingScheduler {

    BookingRepository bookingRepository;

    // this is a quick and dirty fix
    @Scheduled(cron = "0 0/15 * * * ?") // every fifteen minutes
    @Transactional
    public void expireBookings() {
        log.info("looking for bookings to expire");
        List<BookingItem> list = bookingRepository.findAllByBookingStatusIn(List.of(BookingStatus.BOOKED))
                .stream()
                .filter((bookingEntity -> !bookingEntity.getBookedUntil().isAfter(LocalDate.now())))
                .peek(item -> item.setBookingStatus(BookingStatus.EXPIRED))
                .toList();

        log.info("found {} bookings to expire", list.size());

        bookingRepository.saveAll(list);
    }

    // Setting "Pending" Bookings to "Booked" if started
    @Scheduled(cron = "0 0/15 * * * ?") // every fifteen minutes
    @Transactional
    public void startBooking() {
        log.info("looking for bookings to set to booked");
        List<BookingItem> list = bookingRepository.findAllByBookingStatusIn(List.of(BookingStatus.PENDING))
                .stream()
                .filter((bookingEntity -> LocalDateUtils.isBeforeOrEqual(LocalDate.now(), bookingEntity.getBookedFrom())))
                .peek(item -> item.setBookingStatus(BookingStatus.BOOKED))
                .toList();

        log.info("found {} bookings to set from pending to booked", list.size());

        bookingRepository.saveAll(list);
    }
}
