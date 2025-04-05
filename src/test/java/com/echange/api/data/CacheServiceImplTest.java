package com.echange.api.data;


import com.echange.api.data.model.CachedRates;
import com.echange.api.data.model.CurrencyDetails;
import com.echange.api.data.model.CurrencySymbolsResponse;
import com.echange.api.data.model.ExchangeRateResponse;
import com.echange.api.data.service.ValidateService;
import com.echange.api.data.service.impl.CacheServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CacheServiceImplTest {

    @Mock
    private ValidateService validateService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CacheServiceImpl cacheService;

    private ExchangeRateResponse exchangeRateResponse;
    private CurrencySymbolsResponse currencySymbolsResponse;

    @BeforeEach
    public void setUp() {
        exchangeRateResponse = new ExchangeRateResponse("USD", new HashMap<>());
        currencySymbolsResponse = new CurrencySymbolsResponse(true, new HashMap<>());

        when(restTemplate.getForEntity(anyString(), eq(ExchangeRateResponse.class)))
                .thenReturn(ResponseEntity.ok(exchangeRateResponse));
        when(restTemplate.getForEntity(anyString(), eq(CurrencySymbolsResponse.class)))
                .thenReturn(ResponseEntity.ok(currencySymbolsResponse));
    }

    @Test
    public void testGetRatesWhenNotCached() {
        String from = "USD";
        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 0.85);
        exchangeRateResponse.setRates(rates);

        Map<String, Double> cachedRates = cacheService.getRates(from);

        assertNotNull(cachedRates);
        assertEquals(1, cachedRates.size());
        assertEquals(0.85, cachedRates.get("EUR"));

        verify(validateService).validateRates(any());
        verify(restTemplate).getForEntity(anyString(), eq(ExchangeRateResponse.class));
    }

    @Test
    public void testGetRatesWhenCachedAndNotExpired() {
        String from = "USD";
        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 0.85);
        CachedRates cachedRates = new CachedRates(rates);
        cacheService.getCache().put(from.toUpperCase(), cachedRates);

        Map<String, Double> result = cacheService.getRates(from);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(0.85, result.get("EUR"));

        verify(validateService, never()).validateRates(any());
        verify(restTemplate, never()).getForEntity(anyString(), eq(ExchangeRateResponse.class));
    }

    @Test
    public void testGetRatesWhenCachedAndExpired() throws InterruptedException {
        String from = "USD";
        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 0.85);
        CachedRates cachedRates = new CachedRates(rates);
        cacheService.getCache().put(from.toUpperCase(), cachedRates);

        // Simulate expiration
        Thread.sleep(61_000);

        Map<String, Double> newRates = new HashMap<>();
        newRates.put("GBP", 0.75);
        exchangeRateResponse.setRates(newRates);

        Map<String, Double> result = cacheService.getRates(from);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(0.75, result.get("GBP"));

        verify(validateService).validateRates(any());
        verify(restTemplate).getForEntity(anyString(), eq(ExchangeRateResponse.class));
    }

    @Test
    public void testGetAllCurrencies() {
        Map<String, CurrencyDetails> symbols = new HashMap<>();
        symbols.put("USD", new CurrencyDetails("United States Dollar", "USD"));
        currencySymbolsResponse.setSymbols(symbols);

        cacheService.putAllCurrenciesToCache();

        Map<String, String> result = cacheService.getAllCurrencies();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("United States Dollar", result.get("USD"));

        verify(validateService).validateSymbols(any());
        verify(restTemplate).getForEntity(anyString(), eq(CurrencySymbolsResponse.class));
    }

    @Test
    public void testRefreshDataFromUrl() {
        Map<String, String> currencies = new HashMap<>();
        currencies.put("USD", "United States Dollar");
        when(cacheService.getAllCurrencies()).thenReturn(currencies);

        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 0.85);
        exchangeRateResponse.setRates(rates);

        cacheService.refreshDataFromUrl();

        assertTrue(cacheService.getCache().containsKey("USD"));
        assertEquals(rates, cacheService.getCache().get("USD").getRates());

        verify(validateService, times(1)).validateRates(any());
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(ExchangeRateResponse.class));
    }

    @Test
    public void testGetRatesFromCache() {
        String base = "USD";
        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 0.85);
        CachedRates cachedRates = new CachedRates(rates);
        cacheService.getCache().put(base.toUpperCase(), cachedRates);

        Map<String, Double> result = cacheService.getRatesFromCache(base);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(0.85, result.get("EUR"));

        verify(validateService, never()).validateRates(any());
        verify(restTemplate, never()).getForEntity(anyString(), eq(ExchangeRateResponse.class));
    }
}