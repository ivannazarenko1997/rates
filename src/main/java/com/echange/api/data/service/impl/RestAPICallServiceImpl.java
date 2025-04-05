package com.echange.api.data.service.impl;

import com.echange.api.data.model.CachedRates;
import com.echange.api.data.model.CurrencyDetails;
import com.echange.api.data.model.CurrencySymbolsResponse;
import com.echange.api.data.model.ExchangeRateResponse;
import com.echange.api.data.service.RestAPICallService;
import com.echange.api.data.service.ValidateService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service

public class RestAPICallServiceImpl implements RestAPICallService {
    private final ValidateService validateService;
    private final RestTemplate restTemplate;
    private final RetryTemplate retryTemplate;
    private static final Logger log = LoggerFactory.getLogger(RestAPICallServiceImpl.class);
    @Value("${exchange.api-url}")
    private static String apiUrl = "https://api.exchangerate.host";

    @Value("${exchange.additional-api-url}")
    private static String additionalApiUrl = "/latest?base=";

    @Value("${exchange.symbols}")
    private static String symbols = "/symbols";

    private static final String ALL_API_URL_BASE = apiUrl + additionalApiUrl;
    private static final String ALL_API_SYMBOLS_URL = apiUrl + symbols;

    public RestAPICallServiceImpl(ValidateService validateService,
                                  RestTemplate restTemplate,
                                  RetryTemplate retryTemplate) {
        this.validateService = validateService;
        this.restTemplate = restTemplate;
        this.retryTemplate = retryTemplate;
    }

    public ExchangeRateResponse getExchangeRateRest(String base) {
        StringBuilder strUrl = new StringBuilder(ALL_API_URL_BASE).append(base);
        return retryTemplate.execute(context ->
                restTemplate.getForObject(strUrl.toString(), ExchangeRateResponse.class)
        );
    }

    public Map<String, Double> getRates(String base) {
        Map<String, Double> map = new HashMap<>();
        try {
            ExchangeRateResponse response = getExchangeRateRest(base);
            validateService.validateRates(response);
            map.putAll(response.getRates());
        } catch (Exception e) {
            log.error("Cannot  obtain data for rates ", e);
        }

        return map;
    }

    public CurrencySymbolsResponse getAllCurrenciesRest() {
        return retryTemplate.execute(context ->
                restTemplate.getForObject(ALL_API_SYMBOLS_URL, CurrencySymbolsResponse.class)
        );
    }

    public Map<String, CurrencyDetails> getAllCurrenciesToCache() {
        Map<String, CurrencyDetails> map = new HashMap<>();
        try {
            CurrencySymbolsResponse response = getAllCurrenciesRest();
            validateService.validateSymbols(response);

            map.putAll(response.getSymbols());
        } catch (Exception e) {
            log.error("Cannot obtain data for currency  ", e);
        }

        return map;

    }

}
