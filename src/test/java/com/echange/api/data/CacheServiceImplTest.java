package com.echange.api.data;


import com.echange.api.data.model.CachedRates;
import com.echange.api.data.model.CurrencyDetails;
import com.echange.api.data.service.RestAPICallService;
import com.echange.api.data.service.impl.CacheServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CacheServiceImplTest {

    @Mock
    private RestAPICallService restAPICallService;

    @InjectMocks
    private CacheServiceImpl cacheService;

    private Map<String, Double> rates;
    private Map<String, CurrencyDetails> currencies;

    @BeforeEach
    public void setUp() {
        rates = new HashMap<>();
        rates.put("EUR", 0.85);

        currencies = new HashMap<>();
        currencies.put("USD", new CurrencyDetails("United States Dollar", "USD"));
    }

    @Test
    public void testGetRatesWhenNotCached() {
        String from = "USD";
        when(restAPICallService.getRates(from)).thenReturn(rates);

        Map<String, Double> cachedRates = cacheService.getRatesFromCache(from);

        assertNotNull(cachedRates);
        assertEquals(1, cachedRates.size());
        assertEquals(0.85, cachedRates.get("EUR"));

        verify(restAPICallService).getRates(from);
    }

    @Test
    public void testGetRatesWhenCachedAndNotExpired() {
        String from = "USD";
        CachedRates cachedRates = new CachedRates(rates);
        cacheService.putCache(from.toUpperCase(), cachedRates);

        Map<String, Double> result = cacheService.getRatesFromCache(from);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(0.85, result.get("EUR"));

        verify(restAPICallService, never()).getRates(from);
    }

    @Test
    public void testGetRatesWhenCachedAndExpired() throws InterruptedException {
        String from = "USD";
        CachedRates cachedRates = new CachedRates(rates);
        cacheService.putCache(from.toUpperCase(), cachedRates);

        // Simulate expiration
        Thread.sleep(61_000);

        Map<String, Double> newRates = new HashMap<>();
        newRates.put("GBP", 0.75);
        when(restAPICallService.getRates(from)).thenReturn(newRates);

        Map<String, Double> result = cacheService.getRatesFromCache(from);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(0.75, result.get("GBP"));

        verify(restAPICallService).getRates(from);
    }

    @Test
    public void testGetAllCurrenciesFromCache() {
        when(restAPICallService.getAllCurrenciesToCache()).thenReturn(currencies);

        Map<String, String> result = cacheService.getAllCurrenciesFromCache();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("United States Dollar", result.get("USD"));

        verify(restAPICallService).getAllCurrenciesToCache();
    }

    @Test
    public void testPutAllCurrenciesToCache() {
        when(restAPICallService.getAllCurrenciesToCache()).thenReturn(currencies);

        cacheService.putAllCurrenciesToCache();

        Map<String, String> result = cacheService.getAllCurrencies();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("United States Dollar", result.get("USD"));

        verify(restAPICallService).getAllCurrenciesToCache();
    }

    @Test
    public void testRefreshDataFromUrl() {
        when(restAPICallService.getAllCurrenciesToCache()).thenReturn(currencies);
        when(restAPICallService.getRates("USD")).thenReturn(rates);

        cacheService.refreshDataFromUrl();

        assertTrue(cacheService.getCache().containsKey("USD"));
        assertEquals(rates, cacheService.getCache().get("USD").getRates());

        verify(restAPICallService).getAllCurrenciesToCache();
        verify(restAPICallService).getRates("USD");
    }
}