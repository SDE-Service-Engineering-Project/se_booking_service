package at.ac.fhcampuswien.se_booking.service.currency_converter;

import at.ac.fhcampuswien.se_booking.dto.currency.CurrencyDTO;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class CurrencyClient {

    private static final Logger logger = Logger.getLogger(CurrencyClient.class.getName());
    private final ManagedChannel channel;

    private final CurrencyConversionGrpc.CurrencyConversionBlockingStub blockingStub;

    public CurrencyClient() {
        String host = "169.51.206.49";
        int port = 32760;
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.blockingStub = CurrencyConversionGrpc.newBlockingStub(channel);
    }

    public Double convert(String fromCurrency, String toCurrency, Double amount) {
        ConversionRequest request = ConversionRequest.newBuilder()
                .setFromCurrency(fromCurrency)
                .setToCurrency(toCurrency)
                .setAmount(amount)
                .build();
        ConversionResponse response = blockingStub.convert(request);
        logger.log(Level.WARNING, response.toString());
        return response.getAmount();
    }

    public CurrencyDTO getCurrencies() {
        CurrencyRequest request = CurrencyRequest.newBuilder().build();
        CurrencyResponse response = blockingStub.getCurrencies(request);
        logger.log(Level.WARNING, response.getCurrencies(0).getName());
        List<String> currencies = new ArrayList<>();
        for (Currency currency : response.getCurrenciesList()) {
            currencies.add(currency.getName());
        }
        return new CurrencyDTO(currencies);
    }
//    public CurrencyResponse getCurrencies() {
//        CurrencyRequest request = CurrencyRequest.newBuilder()..build();
//        CurrencyResponse response = blockingStub.getCurrencies(request);
//        return response;
//        return new CurrencyDTO(response.getCurrenciesList().stream().map(Currency::getName).toList());
//    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

}