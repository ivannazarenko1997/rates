package com.echange.api.data;


import com.echange.api.data.service.CacheService;
import com.echange.api.data.service.impl.ExchangeRateRetrieveServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mockito;
import org.assertj.core.api.Assertions;

import java.util.concurrent.Executors;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateRetrieveServiceImplTest {

    @Mock
    private CacheService cacheService;

    @Mock
    private Executor virtualThreadExecutor;

    @InjectMocks
    private ExchangeRateRetrieveServiceImpl exchangeRateRetrieveService;

    @BeforeEach
    public void setUp() {
        virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
        exchangeRateRetrieveService = new ExchangeRateRetrieveServiceImpl(cacheService, virtualThreadExecutor);
    }

    @Test
    public void testGetExchangeRateWhenCacheContainsRateThenReturnRate() throws Exception {
        // Arrange
        String from = "USD";
        String to = "EUR";
        double expectedRate = 0.85;
        Map<String, Double> rates = new HashMap<>();
        rates.put(to, expectedRate);

        Mockito.when(cacheService.getRatesFromCache(from)).thenReturn(rates);

        // Act
        double actualRate = exchangeRateRetrieveService.getExchangeRate(from, to);

        // Assert
        Assertions.assertThat(actualRate).isEqualTo(expectedRate);
    }

    @Test
    public void testGetExchangeRateWhenCacheDoesNotContainRateThenReturnZero() throws Exception {
        // Arrange
        String from = "USD";
        String to = "EUR";
        Map<String, Double> rates = new HashMap<>();

        Mockito.when(cacheService.getRatesFromCache(from)).thenReturn(rates);

        // Act
        double actualRate = exchangeRateRetrieveService.getExchangeRate(from, to);

        // Assert
        Assertions.assertThat(actualRate).isEqualTo(0.0);
    }

    @Test
    public void testGetAllExchangeRatesWhenCacheContainsRatesThenReturnRates() {
        // Arrange
        String from = "USD";
        Map<String, Double> expectedRates = new HashMap<>();
        expectedRates.put("EUR", 0.85);
        expectedRates.put("GBP", 0.75);

        Mockito.when(cacheService.getRatesFromCache(from)).thenReturn(expectedRates);

        // Act
        Map<String, Double> actualRates = exchangeRateRetrieveService.getAllExchangeRates(from);

        // Assert
        Assertions.assertThat(actualRates).isEqualTo(expectedRates);
    }

    @Test
    public void testConvertWhenCacheContainsRateThenReturnConvertedAmount() throws Exception {
        // Arrange
        String from = "USD";
        String to = "EUR";
        double amount = 100.0;
        double rate = 0.85;
        double expectedConvertedAmount = amount * rate;
        Map<String, Double> rates = new HashMap<>();
        rates.put(to, rate);

        Mockito.when(cacheService.getRatesFromCache(from)).thenReturn(rates);

        // Act
        double actualConvertedAmount = exchangeRateRetrieveService.convert(from, to, amount);

        // Assert
        Assertions.assertThat(actualConvertedAmount).isEqualTo(expectedConvertedAmount);
    }

    @Test
    public void testConvertToMultipleWhenCacheContainsRatesThenReturnConvertedAmounts() {
        // Arrange
        String from = "USD";
        double amount = 100.0;
        List<String> targets = Arrays.asList("EUR", "GBP");
        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 0.85);
        rates.put("GBP", 0.75);
        Map<String, Double> expectedConvertedAmounts = new HashMap<>();
        expectedConvertedAmounts.put("EUR", amount * 0.85);
        expectedConvertedAmounts.put("GBP", amount * 0.75);

        Mockito.when(cacheService.getRatesFromCache(from)).thenReturn(rates);

        // Act
        Map<String, Double> actualConvertedAmounts = exchangeRateRetrieveService.convertToMultiple(from, amount, targets);

        // Assert
        Assertions.assertThat(actualConvertedAmounts).isEqualTo(expectedConvertedAmounts);
    }
}