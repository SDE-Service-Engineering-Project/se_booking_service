package at.ac.fhcampuswien.se_booking.service.currency_converter;

import at.ac.fhcampuswien.se_booking.dto.currency.ConvertCarPriceDTO;
import at.ac.fhcampuswien.se_booking.dto.currency.ConvertResultDTO;
import at.ac.fhcampuswien.se_booking.dto.currency.CurrencyDTO;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
@Log4j2
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CurrencyConverterServiceImpl implements CurrencyConverterService {

    ManagedChannel channel = Grpc.newChannelBuilder("169.51.206.49:32760", InsecureChannelCredentials.create()).build();
    private static final Logger logger = Logger.getLogger(CurrencyConverterServiceImpl.class.getName());

    CurrencyClient currencyClient;

    @Override
    public CurrencyDTO getAllCurrencies() throws InterruptedException {
        try {
            return currencyClient.getCurrencies();
        } catch (Exception e) {
            logger.warning(e.getMessage());
        } finally {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
        return null;
    }

    @Override
    public ConvertResultDTO convert(Double amount, String fromCurrency, String toCurrency) throws InterruptedException {
        try {
            return new ConvertResultDTO(currencyClient.convert(fromCurrency, toCurrency, amount), toCurrency);
        } catch (Exception e) {
            logger.warning(e.getMessage());
        } finally {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
        return null;
    }

    @Override
    public ConvertResultDTO convertCarPrice(ConvertCarPriceDTO convertCarPriceDTO) {
        return null;
    }
}
