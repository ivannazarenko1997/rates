package com.echange.api.data.model;

import java.util.Map;

public class ExchangeRateResponse {
    public ExchangeRateResponse( ) {
    }
    public ExchangeRateResponse(String base, Map<String, Double> rates) {
        this.base = base;
        this.rates = rates;
    }

    private String base;
    private Map<String, Double> rates;

    public void setBase(String base) {
        this.base = base;
    }

    public void setRates(Map<String, Double> rates) {
        this.rates = rates;
    }

    public String getBase() {
        return base;
    }

    public Map<String, Double> getRates() {
        return rates;
    }
}
