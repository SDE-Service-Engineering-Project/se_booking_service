package at.ac.fhcampuswien.se_booking.service;

import at.ac.fhcampuswien.se_booking.dto.currency.ConvertResultDTO;
import at.ac.fhcampuswien.se_booking.dto.currency.CurrencyDTO;
import at.ac.fhcampuswien.se_booking.service.currency_converter.CurrencyConverterServiceImpl;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public class CurrencyConverterServiceImplTest {
    @InjectMocks
    CurrencyConverterServiceImpl currencyConverterService;

    @Test
    void should_convert_currency() {
        Float amount = 200.0f;
        String fromCurrency = "USD";
        String toCurrenty = "EUR";
        Float resultAmount = 210.0f;

//        Mockito.when(currencyConverterService.convert(amount, fromCurrency, toCurrenty))
//                .thenReturn(resultAmount);
//
//        ConvertResultDTO result = currencyConverterService.convert(amount, fromCurrency, toCurrenty);
//
//        Assertions.assertEquals(resultAmount, result.amount());
//        Assertions.assertEquals(toCurrenty, result.currency());

    }

    @Test
    void should_throw_error_if_something_wrong() {
        Float amount = 200.0f;
        String fromCurrency = "USD";
        String toCurrenty = "EU";

        Mockito.when(currencyConverterService.convert(amount, fromCurrency, toCurrenty))
                .thenThrow(StatusRuntimeException.class);

        Assertions.assertThrows(
                ResponseStatusException.class, () -> {
                    currencyConverterService.convert(amount, fromCurrency, toCurrenty);
                }
        );
    }

    @Test
    void should_get_all_currencies() {
        List<String> listOfCurrencies = List.of("EUR", "USD", "ZAR");

//        StringArray array = Mockito.mock(StringArray.class);
//        Mockito.when(currencyConversionService.listCurrencies())
//                .thenReturn(array);
//        Mockito.when(array.getString()).thenReturn(listOfCurrencies);

        CurrencyDTO result = currencyConverterService.getAllCurrencies();

        Assertions.assertEquals(listOfCurrencies, result.currencies());
    }
}
