package com.echange.api.data.service.impl;

import com.echange.api.data.service.CacheService;
import com.echange.api.data.service.CurrencyService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Service
public class CurrencyServiceImpl implements CurrencyService {

    private final CacheService cacheService;
    private final Executor virtualThreadExecutor;

    public CurrencyServiceImpl(CacheService cacheService, Executor virtualThreadExecutor) {
        this.cacheService = cacheService;
        this.virtualThreadExecutor = virtualThreadExecutor;
    }

    @Override
    public Map<String, String> getAllCurrencies() {
        try {
            Future<Map<String, String>> future = ((ExecutorService) virtualThreadExecutor).submit(() ->
                    cacheService.getAllCurrencies()
            );
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch currencies", e);
        }
    }
}
