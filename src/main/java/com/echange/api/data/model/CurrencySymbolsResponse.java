package com.echange.api.data.model;


import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@ToString
public class CurrencySymbolsResponse {
    private boolean success;
    private Map<String, CurrencyDetails> symbols = new HashMap<>();


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setSymbols(Map<String, CurrencyDetails> symbols) {
        this.symbols = symbols;
    }

    public CurrencySymbolsResponse(boolean success, Map<String, CurrencyDetails> symbols) {
        this.success = success;
        this.symbols = symbols;
    }

    public Map<String, CurrencyDetails> getSymbols() {
        return symbols;
    }
}

