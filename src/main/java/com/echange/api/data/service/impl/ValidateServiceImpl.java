package com.echange.api.data.service.impl;

import com.echange.api.data.model.CurrencySymbolsResponse;
import com.echange.api.data.model.ExchangeRateResponse;
import com.echange.api.data.service.ValidateService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ValidateServiceImpl implements ValidateService {
    public void validateRates(ResponseEntity<ExchangeRateResponse> response) {
        if (response==null || response.getBody()==null) {
            throw new RuntimeException("response is empty");
        } else  if (response.getBody().getRates()==null || response.getBody().getRates().isEmpty()) {
            throw new RuntimeException("rates is empty");
        }
    }

    public void validateSymbols(ResponseEntity<CurrencySymbolsResponse> response) {
        if (response==null || response.getBody()==null) {
            throw new RuntimeException("response is empty");
        } else  if (response.getBody().getSymbols()==null || response.getBody().getSymbols().isEmpty()) {
            throw new RuntimeException("symbols is empty");
        }
    }
}
