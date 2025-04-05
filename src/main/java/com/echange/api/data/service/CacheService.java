package com.echange.api.data.service;

import java.util.Map;

public interface CacheService {
      void refreshDataFromUrl();
      Map<String, String> getAllCurrencies() ;
      Map<String, Double> getRatesFromCache(String base) ;

}
