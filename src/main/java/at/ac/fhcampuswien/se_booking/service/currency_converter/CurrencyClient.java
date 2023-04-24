package at.ac.fhcampuswien.se_booking.service.currency_converter;

import at.ac.fhcampuswien.se_booking.dto.currency.CurrencyDTO;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrencyClient {

    private final ManagedChannel channel;
    private final CurrencyConversionGrpc.CurrencyConversionBlockingStub blockingStub;

    public CurrencyClient(@Value("${grpc.hostname}") String host, @Value("${grpc.port}") int port) {
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
        return response.getAmount();
    }

    public CurrencyDTO getCurrencies() {
        CurrencyRequest request = CurrencyRequest.newBuilder().build();
        CurrencyResponse response = blockingStub.getCurrencies(request);
        return new CurrencyDTO(response.getCurrenciesList().stream().map(Currency::getName).toList());
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

}