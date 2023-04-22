package at.ac.fhcampuswien.se_booking.service.currency_converter;

import at.ac.fhcampuswien.se_booking.dto.currency.ConvertCarPriceDTO;
import at.ac.fhcampuswien.se_booking.dto.currency.ConvertResultDTO;
import at.ac.fhcampuswien.se_booking.dto.currency.CurrencyDTO;

public interface CurrencyConverterService {
    CurrencyDTO getAllCurrencies();

    ConvertResultDTO convert(Float amount, String fromCurrency, String toCurrency);

    ConvertResultDTO convertCarPrice(ConvertCarPriceDTO convertCarPriceDTO);
}
