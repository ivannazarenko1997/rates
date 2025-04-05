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