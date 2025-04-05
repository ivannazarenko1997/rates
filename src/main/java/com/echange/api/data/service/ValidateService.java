package com.echange.api.data.service;

import com.echange.api.data.model.CurrencySymbolsResponse;
import com.echange.api.data.model.ExchangeRateResponse;
import org.springframework.http.ResponseEntity;

public interface ValidateService {
      void validateRates(ExchangeRateResponse response) ;
      void validateSymbols(CurrencySymbolsResponse response);
}
