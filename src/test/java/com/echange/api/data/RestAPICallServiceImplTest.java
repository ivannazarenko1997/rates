package com.echange.api.data;


import com.echange.api.data.model.CurrencyDetails;
import com.echange.api.data.model.CurrencySymbolsResponse;
import com.echange.api.data.model.ExchangeRateResponse;
import com.echange.api.data.service.ValidateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;
import  com.echange.api.data.service.impl.RestAPICallServiceImpl;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RestAPICallServiceImplTest {

    @Mock
    private ValidateService validateService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RetryTemplate retryTemplate;

    @InjectMocks
    private RestAPICallServiceImpl restAPICallService;

    private ExchangeRateResponse exchangeRateResponse;
    private CurrencySymbolsResponse currencySymbolsResponse;

    @BeforeEach
    public void setUp() {
        exchangeRateResponse = new ExchangeRateResponse("USD", new HashMap<>());
        currencySymbolsResponse = new CurrencySymbolsResponse(true, new HashMap<>());
    }

    @Test
    public void testGetRates() {
        String base = "USD";
        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 0.85);
        exchangeRateResponse.setRates(rates);

        when(retryTemplate.execute(any())).thenReturn(exchangeRateResponse);

        Map<String, Double> result = restAPICallService.getRates(base);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(0.85, result.get("EUR"));

        verify(validateService).validateRates(any());
        verify(retryTemplate).execute(any());
    }

    @Test
    public void testGetRatesException() {
        String base = "USD";

        when(retryTemplate.execute(any())).thenThrow(new RuntimeException("API call failed"));

        Map<String, Double> result = restAPICallService.getRates(base);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(validateService, never()).validateRates(any());
        verify(retryTemplate).execute(any());
    }

    @Test
    public void testGetAllCurrenciesToCache() {
        Map<String, CurrencyDetails> symbols = new HashMap<>();
        symbols.put("USD", new CurrencyDetails("United States Dollar", "USD"));
        currencySymbolsResponse.setSymbols(symbols);

        when(retryTemplate.execute(any())).thenReturn(currencySymbolsResponse);

        Map<String, CurrencyDetails> result = restAPICallService.getAllCurrenciesToCache();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("United States Dollar", result.get("USD").getDescription());

        verify(validateService).validateSymbols(any());
        verify(retryTemplate).execute(any());
    }

    @Test
    public void testGetAllCurrenciesToCacheException() {
        when(retryTemplate.execute(any())).thenThrow(new RuntimeException("API call failed"));

        Map<String, CurrencyDetails> result = restAPICallService.getAllCurrenciesToCache();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(validateService, never()).validateSymbols(any());
        verify(retryTemplate).execute(any());
    }
}