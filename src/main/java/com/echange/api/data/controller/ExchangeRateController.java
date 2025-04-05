package com.echange.api.data.controller;

import com.echange.api.data.service.CurrencyService;
import com.echange.api.data.service.ExchangeRateRetrieveService;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api")
@Validated
public class ExchangeRateController {

    @Autowired
    private ExchangeRateRetrieveService service;

    @Autowired
    private CurrencyService currencyService;

    @GetMapping("/rate")
    public double getRate(@RequestParam @NotBlank(message = "Parameter 'from' must not be blank") String from,
                          @RequestParam @NotBlank(message = "Parameter 'to' must not be blank") String to) {
        return service.getExchangeRate(from, to);
    }

    @GetMapping("/rates")
    public Map<String, Double> getAllRates(@RequestParam @NotBlank(message = "Parameter 'from' must not be blank") String from) {
        return service.getAllExchangeRates(from);
    }

    @GetMapping("/convert")
    public double convert(@RequestParam @NotBlank(message = "Parameter 'from' must not be blank") String from,
                          @RequestParam @NotBlank(message = "Parameter 'to' must not be blank") String to,
                          @RequestParam @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0") double amount) {
        return service.convert(from, to, amount);
    }

    @PostMapping("/convert-multiple")
    public Map<String, Double> convertMultiple(@RequestParam @NotBlank(message = "Parameter 'from' must not be blank") String from,
                                               @RequestBody @NotEmpty(message = "Target currency list must not be empty")
                                               List<@NotBlank(message = "Currency codes in the list must not be blank") String> toCurrencies,
                                               @RequestParam @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0") double amount) {
        return service.convertToMultiple(from, amount, toCurrencies);
    }

    @PostMapping("/currency/all")
    public Map<String, String> currencyAll() {
        return currencyService.getAllCurrencies();
    }
}
