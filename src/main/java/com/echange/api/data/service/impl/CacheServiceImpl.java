package com.echange.api.data.service.impl;

import com.echange.api.data.model.CachedRates;
import com.echange.api.data.model.CurrencySymbolsResponse;
import com.echange.api.data.model.ExchangeRateResponse;
import com.echange.api.data.service.CacheService;
import com.echange.api.data.service.ValidateService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class CacheServiceImpl implements CacheService {
    private final ValidateService validateService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final Map<String, CachedRates> cache = new ConcurrentHashMap<>();

    private final Map<String, String> cacheCurrency = new ConcurrentHashMap<>();

    @Value("${exchange.api-url}")
    private static String apiUrl = "https://api.exchangerate.host";

    @Value("${exchange.additional-api-url}")
    private static String additionalApiUrl = "/latest?base=";

    @Value("${exchange.symbols}")
    private static String symbols = "/symbols";

    private static final String ALL_API_URL = apiUrl + additionalApiUrl;
    private static final String ALL_API_SYMBOLS_URL = apiUrl + symbols;

    public CacheServiceImpl(ValidateService validateService) {
        this.validateService = validateService;
    }

    public Map<String, CachedRates> getCache() {
        return cache;
    }

    public Map<String, Double> getRates(String base) {
        StringBuilder strUrl = new StringBuilder(ALL_API_URL).append(base);
        ResponseEntity<ExchangeRateResponse> response = restTemplate.getForEntity(strUrl.toString(), ExchangeRateResponse.class);
        validateService.validateRates(response);
        return response.getBody().getRates();
    }

    public void putAllCurrenciesToCache() {
        ResponseEntity<CurrencySymbolsResponse> response =
                restTemplate.getForEntity(ALL_API_SYMBOLS_URL, CurrencySymbolsResponse.class);
        validateService.validateSymbols(response);

        response.getBody().getSymbols().forEach((key, value) -> {
            cacheCurrency.put(key, value.getDescription());
        });
    }

    public Map<String, String> getAllCurrencies() {
       return cacheCurrency;
    }
    public void refreshDataFromUrl() {
        putAllCurrenciesToCache();
        getAllCurrencies() .entrySet().stream().forEach(e -> {
            String codeCurrency = e.getKey();
            putCachedRates(codeCurrency);
        });
    }


    public Map<String, Double> putCachedRates(String from) {
        CachedRates cached = cache.get(from.toUpperCase());
        if (cached == null || cached.isExpired()) {
            Map<String, Double> rates = getRates(from);
            cached = new CachedRates(rates);
            cache.put(from.toUpperCase(), cached);
        }
        return cached.getRates();
    }


    public Map<String, Double> getRatesFromCache(String base) {
        CachedRates cached = cache.get(base.toUpperCase());
        if (cached == null || cached.isExpired()) {
            Map<String, Double> rates = getRates(base);
            cached = new CachedRates(rates);
            cache.put(base.toUpperCase(), cached);
        }
        return cached.getRates();
    }


}
