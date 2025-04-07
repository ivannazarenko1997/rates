package com.echange.api.data.service.impl;

import com.echange.api.data.exception.CustomValidationException;
import com.echange.api.data.model.CachedRates;
import com.echange.api.data.service.CacheService;
import com.echange.api.data.service.RestAPICallService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class CacheServiceImpl implements CacheService {
    private static final Logger log = LoggerFactory.getLogger(CacheServiceImpl.class);
    private final RestAPICallService restAPICallService;
    private final Executor virtualThreadExecutor;
    private final Map<String, CachedRates> cacheRates = new ConcurrentHashMap<>();
    private final Map<String, String> cacheCurrency = new ConcurrentHashMap<>();

    public CacheServiceImpl(RestAPICallService restAPICallService, Executor virtualThreadExecutor) {
        this.restAPICallService = restAPICallService;
        this.virtualThreadExecutor = virtualThreadExecutor;
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
            log.error("Failed to get currencies from API", e);
            throw new CustomValidationException("Failed to get currencies from API" );
        }
    }

    public void putAllCurrenciesToCache() {
           Map<String, String> map = getAllCurrenciesFromCache();
            if (!validateResult(map)) {
                log.error("Failed to get currencies and rates  from API by sheduler. Map empty");
                throw  new CustomValidationException("Failed to get currencies and rates  from API by sheduler. Map empty");
            }
            cacheCurrency.putAll(map);

    }
    
    private boolean validateResult(Map<String, String> map) {
        return (map != null && !map.isEmpty()) ;
    }

    public Map<String, String> getAllCurrencies() {
        return cacheCurrency;
    }

    public void refreshDataFromUrl() {
        try {
            putAllCurrenciesToCache();
            getAllCurrencies().keySet().forEach(this::putCachedRates);
            log.info("Obtained  succesfully currencies and rates from API by sheduler" );
        } catch (Exception e) {
            log.error("Failed to get currencies and rates  from API by sheduler", e);
        }
    }

    public void putCachedRates(String from) {
        getRatesFromCache(from);
    }

    public Map<String, Double> getRatesFromCache(String base) {
        try {
            Future<Map<String, Double>> future = ((ExecutorService) virtualThreadExecutor).submit(() -> {
                CachedRates cached = cacheRates.get(base.toUpperCase());
                if (cached == null || cached.isExpired()) {
                    Map<String, Double> rates = restAPICallService.getRates(base);

                    if (!validateRates(rates)) {
                        log.error("Data from API for rates empty");
                        return new HashMap<>();
                    }
                    cached = new CachedRates(rates);
                    putCache(base.toUpperCase(), cached);
                }
                return cached.getRates();
            });
            return future.get();
        } catch (Exception e) {
            log.error("Failed to fetch rates from cache", e);
             return new HashMap<>();
        }
    }

    private boolean validateRates(Map<String, Double> rates) {
        return (rates !=null && !rates.isEmpty());
    }
}