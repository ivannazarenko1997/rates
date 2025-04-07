package com.echange.api.data.model;

import java.util.Map;

public class CachedRates {
    private final Map<String, Double> rates;
    private final long timestamp;

    public CachedRates(Map<String, Double> rates) {
        this.rates = rates;
        this.timestamp = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - timestamp > 60_000;
    }

    public Map<String, Double> getRates() {
        return rates;
    }
}