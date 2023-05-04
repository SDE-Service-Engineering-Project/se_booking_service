package at.ac.fhcampuswien.se_booking.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus responseStatus = HttpStatus.valueOf(response.status());

        if (responseStatus.is5xxServerError()) {
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not fetch car from car service!");
        } else if (responseStatus.is4xxClientError()) {
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find car from car service!");
        } else {
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something went wrong on car service");
        }
    }
}
