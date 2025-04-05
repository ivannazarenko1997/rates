package com.echange.api.data.service.impl;


import com.echange.api.data.service.CacheService;
import com.echange.api.data.service.ExchangeRateRetrieveService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExchangeRateRetrieveServiceImpl implements ExchangeRateRetrieveService {

    private final CacheService cacheService;

    public ExchangeRateRetrieveServiceImpl(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    public double getExchangeRate(String from, String to) {
        Map<String, Double> rates = cacheService.getRatesFromCache(from);
        return rates.getOrDefault(to.toUpperCase(), 0.0);
    }

    public Map<String, Double> getAllExchangeRates(String from) {
        return cacheService.getRatesFromCache(from);
    }

    public double convert(String from, String to, double amount) {
        double rate = getExchangeRate(from, to);
        return rate * amount;
    }

    public Map<String, Double> convertToMultiple(String from, double amount,  List<String> targets) {
        Map<String, Double> result = new HashMap<>();
        Map<String, Double> rates = cacheService.getRatesFromCache(from);

        for (String to : targets) {
            result.put(to.toUpperCase(), rates.getOrDefault(to.toUpperCase(), 0.0) * amount);

        }

        return result;
    }


}