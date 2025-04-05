package com.echange.api.data.service;

import com.echange.api.data.model.CurrencySymbolsResponse;
import com.echange.api.data.model.ExchangeRateResponse;

import java.util.List;


public interface ValidateService {
      void validateRates(ExchangeRateResponse response) ;
      void validateSymbols(CurrencySymbolsResponse response);
      boolean isStringEmpty(String str) ;
      boolean isListEmpty(List<String> list);
}
