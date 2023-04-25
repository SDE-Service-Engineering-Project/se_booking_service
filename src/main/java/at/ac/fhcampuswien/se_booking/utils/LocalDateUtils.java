package at.ac.fhcampuswien.se_booking.utils;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;

@UtilityClass
public class LocalDateUtils {

    /**
     * Checks if two time spans (startA, endA & startB, endB) overlap
     * @return true if it overlaps, false if not
     */
    public boolean isOverlapping(LocalDate startA, LocalDate endA, LocalDate startB, LocalDate endB) {
        return !startA.isAfter(endB) && !startB.isAfter(endA);
    }

    public void validateTimespan(LocalDate start, LocalDate end) {
        LocalDate now = LocalDate.now();
        if(!start.isBefore(end) || !isBeforeOrEqual(start, now)  || !isBeforeOrEqual(end, now)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Timespan is not correct!");
        }
    }

    public boolean isBeforeOrEqual(LocalDate date, LocalDate compareToDate) {
        if (date == null || compareToDate == null) {
            return false;
        }
        return compareToDate.isBefore(date) || compareToDate.isEqual(date);
    }
}
