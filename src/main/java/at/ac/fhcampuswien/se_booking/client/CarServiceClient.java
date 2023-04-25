package at.ac.fhcampuswien.se_booking.client;

import at.ac.fhcampuswien.se_booking.config.FeignClientConfig;
import at.ac.fhcampuswien.se_booking.dto.CarDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "carService", url = "${car_rental.url}", configuration = FeignClientConfig.class)
public interface CarServiceClient {
    @GetMapping(value = "/cars/{id}")
    CarDTO getCarById(@PathVariable String id);
}
