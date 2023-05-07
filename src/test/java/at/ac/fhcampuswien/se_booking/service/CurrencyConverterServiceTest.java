package at.ac.fhcampuswien.se_booking.service;

import at.ac.fhcampuswien.se_booking.client.CarServiceClient;
import at.ac.fhcampuswien.se_booking.dto.CarDTO;
import at.ac.fhcampuswien.se_booking.dto.currency.ConvertCarPriceDTO;
import at.ac.fhcampuswien.se_booking.dto.currency.ConvertResultDTO;
import at.ac.fhcampuswien.se_booking.dto.currency.CurrencyDTO;
import at.ac.fhcampuswien.se_booking.grpc_clients.CurrencyClient;
import at.ac.fhcampuswien.se_booking.service.currency.CurrencyConverterServiceImpl;
import at.ac.fhcampuswien.se_booking.utils.Utils;
import feign.RetryableException;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.testing.GrpcCleanupRule;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class CurrencyConverterServiceTest {
    @Mock
    CarServiceClient carServiceClient;

    @Mock
    CurrencyClient currencyClient;

    @InjectMocks
    CurrencyConverterServiceImpl currencyConverterService;

    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    @Test
    void should_convert_currency() {
        Float amount = 200.0f;
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        Float resultAmount = 210.0f;

        Mockito.when(currencyClient.convert(fromCurrency, toCurrency, amount)).thenReturn(resultAmount);

        ConvertResultDTO result = currencyConverterService.convert(amount, fromCurrency, toCurrency);

        Assertions.assertEquals(resultAmount, result.amount());
        Assertions.assertEquals(toCurrency, result.currency());

    }

    @Test
    void should_throw_error_if_conversion_fails() {
        Float amount = 200.0f;
        String fromCurrency = "USD";
        String toCurrency = "EU";

        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);

        Mockito.when(currencyClient.convert(fromCurrency, toCurrency, amount))
                .thenThrow(exception);

        Assertions.assertThrows(
                ResponseStatusException.class, () -> {
                    currencyConverterService.convert(amount, fromCurrency, toCurrency);
                }
        );
    }

    @Test
    void should_throw_error_if_get_all_currencies_fails() {
        StatusRuntimeException exception = new StatusRuntimeException(Status.NOT_FOUND);

        Mockito.when(currencyClient.getCurrencies()).thenThrow(exception);

        Assertions.assertThrows(
                ResponseStatusException.class, () -> {
                    currencyConverterService.getAllCurrencies();
                }
        );
    }

    @Test
    void should_get_all_currencies() {
        List<String> listOfCurrencies = List.of("EUR", "USD", "ZAR");

        Mockito.when(currencyClient.getCurrencies()).thenReturn(new CurrencyDTO(listOfCurrencies));

        CurrencyDTO result = currencyConverterService.getAllCurrencies();

        Assertions.assertEquals(listOfCurrencies, result.currencies());
    }

    @Test
    void should_convert_car_price() {
        CarDTO carDTO = Utils.carDTO();
        ConvertCarPriceDTO convertCarPriceDTO = new ConvertCarPriceDTO(carDTO.carId(), "EUR");
        float expectedResult = carDTO.price() + 10.0f;

        Mockito.when(carServiceClient.getCarById(Mockito.any())).thenReturn(carDTO);
        Mockito.when(currencyClient.convert(carDTO.currency(), convertCarPriceDTO.toCurrency(), carDTO.price()))
                .thenReturn(expectedResult);

        ConvertResultDTO result = currencyConverterService.convertCarPrice(convertCarPriceDTO);

        Assertions.assertEquals(expectedResult, result.amount());
        Assertions.assertEquals(convertCarPriceDTO.toCurrency(), result.currency());
    }

    @Test
    void should_not_convert_non_existing_car() {
        ConvertCarPriceDTO convertCarPriceDTO = new ConvertCarPriceDTO("0", "EUR");

        Mockito.when(carServiceClient.getCarById(convertCarPriceDTO.carId()))
                .thenThrow(RetryableException.class);


        Assertions.assertThrows(
                ResponseStatusException.class, () -> {
                    currencyConverterService.convertCarPrice(convertCarPriceDTO);
                }
        );
    }
}
