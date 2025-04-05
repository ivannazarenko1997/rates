package com.echange.api.data.service;

import com.echange.api.data.model.CurrencyDetails;
import com.echange.api.data.model.CurrencySymbolsResponse;
import com.echange.api.data.model.ExchangeRateResponse;

import java.util.Map;

public interface RestAPICallService {

      Map<String, Double> getRates(String base);
      Map<String, CurrencyDetails>  getAllCurrenciesToCache();
      ExchangeRateResponse getExchangeRateRest(String base) ;
        CurrencySymbolsResponse getAllCurrenciesRest() ;
}
