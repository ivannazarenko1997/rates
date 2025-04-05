package com.echange.api.data.service.impl;

import com.echange.api.data.service.CacheService;
import com.echange.api.data.service.ExchangeRateRetrieveService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

@Service
public class ExchangeRateRetrieveServiceImpl implements ExchangeRateRetrieveService {

    private final CacheService cacheService;
    private final Executor virtualThreadExecutor;

    public ExchangeRateRetrieveServiceImpl(CacheService cacheService, Executor virtualThreadExecutor) {
        this.cacheService = cacheService;
        this.virtualThreadExecutor = virtualThreadExecutor;
    }

    public double getExchangeRate(String from, String to) {
        try {
            Future<Double> future = ((java.util.concurrent.ExecutorService) virtualThreadExecutor).submit(() -> {
                Map<String, Double> rates = cacheService.getRatesFromCache(from);
                return rates.getOrDefault(to.toUpperCase(), 0.0);
            });
            return future.get(); // blocks briefly, on a virtual thread
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch exchange rate", e);
        }
    }

    public Map<String, Double> getAllExchangeRates(String from) {
        return cacheService.getRatesFromCache(from); // optionally wrap in virtual thread if expensive
    }

    public double convert(String from, String to, double amount) {
        double rate = getExchangeRate(from, to);
        return rate * amount;
    }

    public Map<String, Double> convertToMultiple(String from, double amount, List<String> targets) {
        Map<String, Double> result = new HashMap<>();
        Map<String, Double> rates = cacheService.getRatesFromCache(from);

        for (String to : targets) {
            result.put(to.toUpperCase(), rates.getOrDefault(to.toUpperCase(), 0.0) * amount);
        }

        return result;
    }
}
