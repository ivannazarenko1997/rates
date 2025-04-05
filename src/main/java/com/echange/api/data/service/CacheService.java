package com.echange.api.data.service;

import java.util.Map;

public interface CacheService {
    Map<String, Double> getCachedRates(String from) ;
      void refreshDataFromUrl();
      Map<String, String> getAllCurrencies() ;
}
