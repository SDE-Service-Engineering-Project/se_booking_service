package at.ac.fhcampuswien.se_booking;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Booking Service", version = "1.0"))
public class SeBookingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeBookingServiceApplication.class, args);
    }

}
