package com.echange.api.data.controller;

import com.echange.api.data.service.CurrencyService;
import com.echange.api.data.service.ExchangeRateRetrieveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api")
public class ExchangeRateController {

    @Autowired
    private ExchangeRateRetrieveService service;

    @Autowired
    private  CurrencyService currencyService;
    @GetMapping("/rate")
    public double getRate(@RequestParam String from, @RequestParam String to) {
        return service.getExchangeRate(from, to);
    }

    @GetMapping("/rates")
    public Map<String, Double> getAllRates(@RequestParam String from) {
        return service.getAllExchangeRates(from);
    }

    @GetMapping("/convert")
    public double convert(@RequestParam String from, @RequestParam String to, @RequestParam double amount) {
        return service.convert(from, to, amount);
    }

    @PostMapping("/convert-multiple")
    public Map<String, Double> convertMultiple(@RequestParam String from,
                                               @RequestBody List<String> toCurrencies,
                                               @RequestParam double amount) {
        return service.convertToMultiple(from, amount, toCurrencies);

    }

    @PostMapping("/currency/all")
    public Map<String, String> currencyAll() {
        return currencyService.getAllCurrencies();

    }


}
