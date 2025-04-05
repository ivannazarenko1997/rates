package com.echange.api.data.service.impl;

import com.echange.api.data.model.CurrencyDetails;
import com.echange.api.data.model.CurrencySymbolsResponse;
import com.echange.api.data.model.ExchangeRateResponse;
import com.echange.api.data.service.RestAPICallService;
import com.echange.api.data.service.ValidateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Service
public class RestAPICallServiceImpl implements RestAPICallService {
    private static final Logger log = LoggerFactory.getLogger(RestAPICallServiceImpl.class);
    private final ValidateService validateService;
    private final RestTemplate restTemplate;
    private final RetryTemplate retryTemplate;

    private final ExecutorService virtualThreadExecutor;


    @Value("${exchange.api-url:https://api.exchangerate.host}")
    private String apiUrl;

    @Value("${exchange.additional-api-url:/latest?base=}")
    private String additionalApiUrl;

    @Value("${exchange.symbols:/symbols}")
    private String symbols;

    public RestAPICallServiceImpl(ValidateService validateService,
                                  RestTemplate restTemplate,
                                  RetryTemplate retryTemplate,
                                  ExecutorService virtualThreadExecutor) {
        this.validateService = validateService;
        this.restTemplate = restTemplate;
        this.retryTemplate = retryTemplate;
        this.virtualThreadExecutor = virtualThreadExecutor;
    }

    private String getExchangeUrl(String base) {
        return apiUrl + additionalApiUrl + base;
    }

    private String getSymbolsUrl() {
        return apiUrl + symbols;
    }

    @Override
    public ExchangeRateResponse getExchangeRateRest(String base) {
        try {
            Future<ExchangeRateResponse> future = virtualThreadExecutor.submit(() ->
                    retryTemplate.execute(context ->
                            restTemplate.getForObject(getExchangeUrl(base), ExchangeRateResponse.class)
                    )
            );
            return future.get();
        } catch (Exception e) {
            log.error("Error fetching exchange rate", e);
            return null;
        }
    }

    @Override
    public Map<String, Double> getRates(String base) {
        Map<String, Double> map = new HashMap<>();
        try {
            ExchangeRateResponse response = getExchangeRateRest(base);
            validateService.validateRates(response);
            map.putAll(response.getRates());
        } catch (Exception e) {
            log.error("Cannot obtain data for rates", e);
        }
        return map;
    }

    @Override
    public CurrencySymbolsResponse getAllCurrenciesRest() {
        try {
            Future<CurrencySymbolsResponse> future = virtualThreadExecutor.submit(() ->
                    retryTemplate.execute(context ->
                            restTemplate.getForObject(getSymbolsUrl(), CurrencySymbolsResponse.class)
                    )
            );
            return future.get();
        } catch (Exception e) {
            log.error("Error fetching currency symbols", e);
            return null;
        }
    }

    @Override
    public Map<String, CurrencyDetails> getAllCurrenciesToCache() {
        Map<String, CurrencyDetails> map = new HashMap<>();
        try {
            CurrencySymbolsResponse response = getAllCurrenciesRest();
            validateService.validateSymbols(response);
            map.putAll(response.getSymbols());
        } catch (Exception e) {
            log.error("Cannot obtain data for currency", e);
        }
        return map;
    }
}
