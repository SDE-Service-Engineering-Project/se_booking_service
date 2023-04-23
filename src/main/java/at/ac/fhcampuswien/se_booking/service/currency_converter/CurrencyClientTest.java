//package at.ac.fhcampuswien.se_booking.service.currency_converter;
//
//import at.ac.fhcampuswien.se_booking.dto.currency.CurrencyDTO;
//import at.ac.fhcampuswien.se_booking.prototest.Currency;
//import at.ac.fhcampuswien.se_booking.prototest.CurrencyConversionGrpc;
//import at.ac.fhcampuswien.se_booking.prototest.CurrencyRequest;
//import at.ac.fhcampuswien.se_booking.prototest.CurrencyResponse;
//import io.grpc.*;
//import io.grpc.stub.StreamObserver;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//@Component
//public class CurrencyClient {
//
//    private static final Logger logger = Logger.getLogger(CurrencyClient.class.getName());
//    private final CurrencyConversionGrpc.CurrencyConversionBlockingStub blockingStub;
//    private final CurrencyConversionGrpc.CurrencyConversionStub asyncStub;
//    StreamObserver<CurrencyResponse> = new StreamObserver<CurrencyResponse>() {
//
//    }
//
//    public CurrencyClient() {
//        ManagedChannel channel = Grpc.newChannelBuilder("169.51.206.49:32760", InsecureChannelCredentials.create()).build();
//        blockingStub = CurrencyConversionGrpc.newBlockingStub(channel);
//        asyncStub = CurrencyConversionGrpc.newStub(channel);
//    }
//
//    public CurrencyDTO getCurrencies() {
//        CurrencyRequest request = CurrencyRequest.newBuilder().build();
//        try {
//            CurrencyResponse response = blockingStub.getCurrencies(request);
//            CurrencyResponse response = asyncStub.getCurrencies(request,);
//            return new CurrencyDTO(response.getCurrenciesList().stream().map(Currency::getName).toList());
//
//        } catch (StatusRuntimeException e) {
//            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
//        }
//       return null;
//    }
//
//    public CurrencyDTO getCurrenciesAsync() {
//        CurrencyRequest request = CurrencyRequest.newBuilder().build();
//        StreamObserver<CurrencyResponse> requestObserver = asyncStub.getCurrencies(responseObserver);
//        try {
//            CurrencyResponse response = blockingStub.getCurrencies(request);
//            return new CurrencyDTO(response.getCurrenciesList().stream().map(Currency::getName).toList());
//
//        } catch (StatusRuntimeException e) {
//            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
//        }
//       return null;
//    }
//}
