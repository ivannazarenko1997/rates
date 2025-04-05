package com.echange.api.data.service.impl;

import com.echange.api.data.model.CachedRates;
import com.echange.api.data.service.CacheService;
import com.echange.api.data.service.RestAPICallService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class CacheServiceImpl implements CacheService {

    private final RestAPICallService restAPICallService;
    private final Executor virtualThreadExecutor;
    private final Map<String, CachedRates> cacheRates = new ConcurrentHashMap<>();
    private final Map<String, String> cacheCurrency = new ConcurrentHashMap<>();

    public CacheServiceImpl(RestAPICallService restAPICallService, Executor virtualThreadExecutor) {
        this.restAPICallService = restAPICallService;
        this.virtualThreadExecutor = virtualThreadExecutor;
    }

    public Map<String, CachedRates> getCache() {
        return cacheRates;
    }

    public void putCache(String key, CachedRates value) {
        cacheRates.put(key, value);
    }

    public Map<String, String> getAllCurrenciesFromCache() {
        try {
            Future<Map<String, String>> future = ((ExecutorService) virtualThreadExecutor).submit(() ->
                    restAPICallService.getAllCurrenciesToCache()
                            .entrySet().stream()
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    entry -> entry.getValue().getDescription()
                            ))
            );
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get currencies from API", e);
        }
    }

    public void putAllCurrenciesToCache() {
        cacheCurrency.putAll(getAllCurrenciesFromCache());
    }

    public Map<String, String> getAllCurrencies() {
        return cacheCurrency;
    }

    public void refreshDataFromUrl() {
        putAllCurrenciesToCache();

        getAllCurrencies().keySet().forEach(this::putCachedRates);
    }

    public Map<String, Double> putCachedRates(String from) {
        try {
            Future<Map<String, Double>> future = ((ExecutorService) virtualThreadExecutor).submit(() -> {
                CachedRates cached = cacheRates.get(from.toUpperCase());
                if (cached == null || cached.isExpired()) {
                    Map<String, Double> rates = restAPICallService.getRates(from);
                    cached = new CachedRates(rates);
                    cacheRates.put(from.toUpperCase(), cached);
                }
                return cached.getRates();
            });
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch rates", e);
        }
    }

    public Map<String, Double> getRatesFromCache(String base) {
        try {
            Future<Map<String, Double>> future = ((ExecutorService) virtualThreadExecutor).submit(() -> {
                CachedRates cached = cacheRates.get(base.toUpperCase());
                if (cached == null || cached.isExpired()) {
                    Map<String, Double> rates = restAPICallService.getRates(base);
                    cached = new CachedRates(rates);
                    cacheRates.put(base.toUpperCase(), cached);
                }
                return cached.getRates();
            });
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch rates from cache", e);
        }
    }
}