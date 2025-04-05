package com.echange.api.data.service.impl;

import com.echange.api.data.exception.CustomValidationException;
import com.echange.api.data.model.CurrencySymbolsResponse;
import com.echange.api.data.model.ExchangeRateResponse;
import com.echange.api.data.service.ValidateService;
import org.springframework.stereotype.Service;

@Service
public class ValidateServiceImpl implements ValidateService {
    public void validateRates(ExchangeRateResponse response) throws CustomValidationException {
        if (response==null) {
            throw new CustomValidationException("response is empty");
        } else  if (response.getRates()==null || response.getRates().isEmpty()) {
            throw new CustomValidationException("rates is empty");
        }
    }

    public void validateSymbols(CurrencySymbolsResponse response) throws CustomValidationException{
        if (response==null ) {
            throw new CustomValidationException("response is empty");
        } else  if (response.getSymbols()==null || response.getSymbols().isEmpty()) {
            throw new CustomValidationException("symbols is empty");
        }
    }
}
