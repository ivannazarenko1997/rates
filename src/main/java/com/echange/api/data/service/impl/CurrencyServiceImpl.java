package com.echange.api.data.service.impl;

import com.echange.api.data.service.CacheService;
import com.echange.api.data.service.CurrencyService;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public class CurrencyServiceImpl implements CurrencyService {
    private final CacheService cacheService;

    public CurrencyServiceImpl(CacheService cacheService) {
        this.cacheService = cacheService;
    }
    @Override
    public Map<String, String> getAllCurrencies() {
        return cacheService.getCurrenciesFromCache();
    }
}
