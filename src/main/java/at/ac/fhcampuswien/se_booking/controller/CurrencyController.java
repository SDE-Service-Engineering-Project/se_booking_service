package at.ac.fhcampuswien.se_booking.controller;

import at.ac.fhcampuswien.se_booking.dto.currency.ConvertCarPriceDTO;
import at.ac.fhcampuswien.se_booking.dto.currency.ConvertResultDTO;
import at.ac.fhcampuswien.se_booking.dto.currency.CurrencyDTO;
import at.ac.fhcampuswien.se_booking.service.currency.CurrencyConverterService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/currency")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CurrencyController {


    CurrencyConverterService currencyConverterService;

    @Operation(summary = "Get all possible Currencies")
    @GetMapping("/")
    public ResponseEntity<CurrencyDTO> getAllCurrencies() {
        return ResponseEntity.ok(currencyConverterService.getAllCurrencies());
    }

    @Operation(summary = "Convert from one currency to another")
    @GetMapping("/convert")
    public ResponseEntity<ConvertResultDTO> convert(@RequestParam Float amount, @RequestParam String fromCurrency, @RequestParam String toCurrency) {
        return ResponseEntity.ok(currencyConverterService.convert(amount, fromCurrency, toCurrency));
    }

    @GetMapping("/car")
    public ResponseEntity<ConvertResultDTO> convertCarPrice(@RequestBody ConvertCarPriceDTO convertCarPriceDTO) {
        return ResponseEntity.ok(currencyConverterService.convertCarPrice(convertCarPriceDTO));
    }
}
