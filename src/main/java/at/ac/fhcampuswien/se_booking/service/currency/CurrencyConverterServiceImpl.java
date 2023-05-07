package at.ac.fhcampuswien.se_booking.service.currency;

import at.ac.fhcampuswien.se_booking.client.CarServiceClient;
import at.ac.fhcampuswien.se_booking.dto.CarDTO;
import at.ac.fhcampuswien.se_booking.dto.currency.ConvertCarPriceDTO;
import at.ac.fhcampuswien.se_booking.dto.currency.ConvertResultDTO;
import at.ac.fhcampuswien.se_booking.dto.currency.CurrencyDTO;
import at.ac.fhcampuswien.se_booking.grpc_clients.CurrencyClient;
import feign.RetryableException;
import io.grpc.StatusRuntimeException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Log4j2
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CurrencyConverterServiceImpl implements CurrencyConverterService {

    final CurrencyClient currencyClient;
    final CarServiceClient carServiceClient;

    @Override
    public CurrencyDTO getAllCurrencies() {
        try {
            return currencyClient.getCurrencies();
        } catch (StatusRuntimeException e) {
            log.error("GRPC Error: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not process conversion");
        }
    }

    @Override
    public ConvertResultDTO convert(Float amount, String fromCurrency, String toCurrency) {
        try {
            return new ConvertResultDTO(currencyClient.convert(fromCurrency, toCurrency, amount), toCurrency);
        } catch (StatusRuntimeException e) {
            log.error("GRPC Error: " + e.getMessage());
            if (e.getMessage().contains("is not supported")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provided Currencies not supported");
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not process conversion");
        }
    }

    @Override
    public ConvertResultDTO convertCarPrice(ConvertCarPriceDTO convertCarPriceDTO) {
        try {
            CarDTO car = carServiceClient.getCarById(convertCarPriceDTO.carId());
            return convert(
                    car.price(), car.currency(), convertCarPriceDTO.toCurrency()
            );
        } catch (RetryableException e) {
            log.error("CAR ID NOT FOUND: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find car with id " + convertCarPriceDTO.carId());
        }
    }
}
