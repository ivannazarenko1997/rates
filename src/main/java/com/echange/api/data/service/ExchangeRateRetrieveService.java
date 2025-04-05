package com.echange.api.data.service;

import java.util.List;
import java.util.Map;

public interface ExchangeRateRetrieveService {
    double getExchangeRate(String from, String to);

    Map<String, Double> getAllExchangeRates(String from);

    double convert(String from, String to, double amount);

    Map<String, Double> convertToMultiple(String from, double amount, List<String> targets);


}
