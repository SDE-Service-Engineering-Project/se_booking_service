package at.ac.fhcampuswien.se_booking.controller;

import at.ac.fhcampuswien.se_booking.dto.currency.ConvertResultDTO;
import at.ac.fhcampuswien.se_booking.dto.currency.CurrencyDTO;
import at.ac.fhcampuswien.se_booking.service.currency_converter.CurrencyConverterService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/currencies")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CurrencyController {


    CurrencyConverterService currencyConverterService;

    @Operation(summary = "Get all possible Currencies")
    @GetMapping("/")
    public ResponseEntity<CurrencyDTO> getAllCurrencies() throws InterruptedException {
        return ResponseEntity.ok(currencyConverterService.getAllCurrencies());
    }

    @Operation(summary = "Convert from one currency to another")
    @GetMapping("/convert")
    public ResponseEntity<ConvertResultDTO> convert(@RequestParam Double amount, @RequestParam String fromCurrency, @RequestParam String toCurrency) throws InterruptedException {
        return ResponseEntity.ok(currencyConverterService.convert(amount, fromCurrency, toCurrency));
    }

}
