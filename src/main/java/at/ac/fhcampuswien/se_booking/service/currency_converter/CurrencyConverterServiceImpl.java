package at.ac.fhcampuswien.se_booking.service.currency_converter;

import at.ac.fhcampuswien.se_booking.dto.currency.ConvertCarPriceDTO;
import at.ac.fhcampuswien.se_booking.dto.currency.ConvertResultDTO;
import at.ac.fhcampuswien.se_booking.dto.currency.CurrencyDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CurrencyConverterServiceImpl implements CurrencyConverterService {

    @Override
    public CurrencyDTO getAllCurrencies() {
        return null;
    }

    @Override
    public ConvertResultDTO convert(Float amount, String fromCurrency, String toCurrency) {
        return null;
    }

    @Override
    public ConvertResultDTO convertCarPrice(ConvertCarPriceDTO convertCarPriceDTO) {
        return null;
    }
}
