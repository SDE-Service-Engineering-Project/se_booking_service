package at.ac.fhcampuswien.se_booking.utils;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class LocalDateUtilsTest {

    @Test
    void should_return_true_because_overlapping() {
        LocalDate startA = LocalDate.now().minusDays(5);
        LocalDate endA = LocalDate.now().minusDays(2);
        LocalDate startB = LocalDate.now().minusDays(4);
        LocalDate endB = LocalDate.now().minusDays(1);

        boolean result = LocalDateUtils.isOverlapping(startA, endA, startB, endB);

        Assertions.assertTrue(result);
    }

    @Test
    void should_return_false_because_not_overlapping() {
        LocalDate startA = LocalDate.now().minusDays(5);
        LocalDate endA = LocalDate.now().minusDays(3);
        LocalDate startB = LocalDate.now().minusDays(2);
        LocalDate endB = LocalDate.now().minusDays(1);

        boolean result = LocalDateUtils.isOverlapping(startA, endA, startB, endB);

        Assertions.assertFalse(result);
    }

    @Test
    void should_not_throw_error_to_valid_timestamp() {
        LocalDate start = LocalDate.now().plusDays(3);
        LocalDate end = LocalDate.now().plusDays(5);

        try {
            LocalDateUtils.validateTimespan(start, end);
        } catch (Exception e) {
            Assertions.fail("Should not have thrown any exception");
        }
    }

    @Test
    void should_not_throw_error_to_todays_timestamp() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(5);

        try {
            LocalDateUtils.validateTimespan(start, end);
        } catch (Exception e) {
            Assertions.fail("Should not have thrown any exception");
        }
    }

    @Test
    void should_throw_error_to_invalid_timestamp() {
        LocalDate start = LocalDate.now().plusDays(6);
        LocalDate end = LocalDate.now().plusDays(5);


        Assertions.assertThrows(
                ResponseStatusException.class, () -> LocalDateUtils.validateTimespan(start, end)
        );
    }

    @Test
    void should_throw_error_to_past_start_and_end_date() {
        LocalDate start = LocalDate.now().minusDays(3);
        LocalDate end = LocalDate.now().minusDays(5);


        Assertions.assertThrows(
                ResponseStatusException.class, () -> LocalDateUtils.validateTimespan(start, end)
        );
    }

    @Test
    void should_throw_error_to_past_start_date() {
        LocalDate start = LocalDate.now().minusDays(3);
        LocalDate end = LocalDate.now().plusDays(5);


        Assertions.assertThrows(
                ResponseStatusException.class, () -> LocalDateUtils.validateTimespan(start, end)
        );
    }
}
