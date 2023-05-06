package at.ac.fhcampuswien.se_booking.grpc_clients;

import at.ac.fhcampuswien.se_booking.dto.currency.CurrencyDTO;
import at.ac.fhcampuswien.se_booking.service.currency_converter.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrencyClient {

    private final CurrencyConversionGrpc.CurrencyConversionBlockingStub blockingStub;

    public CurrencyClient(@Value("${grpc.hostname}") String host, @Value("${grpc.port}") int port) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.blockingStub = CurrencyConversionGrpc.newBlockingStub(channel);
    }

    public CurrencyDTO getCurrencies() throws StatusRuntimeException {
        CurrencyRequest request = CurrencyRequest.newBuilder().build();
        CurrencyResponse response = blockingStub.getCurrencies(request);
        return new CurrencyDTO(response.getCurrenciesList().stream().map(Currency::getName).toList());
    }

    public Float convert(String fromCurrency, String toCurrency, Float amount) throws StatusRuntimeException {
        ConversionRequest request = ConversionRequest.newBuilder()
                .setFromCurrency(fromCurrency)
                .setToCurrency(toCurrency)
                .setAmount(amount)
                .build();
        ConversionResponse response = blockingStub.convert(request);
        return (float) response.getAmount();
    }

}