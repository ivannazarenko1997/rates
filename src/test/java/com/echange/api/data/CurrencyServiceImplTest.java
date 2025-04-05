package com.echange.api.data;

import com.echange.api.data.service.CacheService;
import com.echange.api.data.service.impl.CurrencyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceImplTest {

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private CurrencyServiceImpl currencyService;

    private Map<String, String> currencies;

    @BeforeEach
    public void setUp() {
        currencies = new HashMap<>();
        currencies.put("USD", "United States Dollar");
        currencies.put("EUR", "Euro");

        when(cacheService.getCurrenciesFromCache()).thenReturn(currencies);
    }

    @Test
    public void testGetAllCurrencies() {
        // Act
        Map<String, String> result = currencyService.getAllCurrencies();

        // Assert
        assertEquals(currencies, result);
        verify(cacheService, times(1)).getCurrenciesFromCache();
    }
}