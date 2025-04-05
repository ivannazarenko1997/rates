package com.echange.api.data.service.impl;

import com.echange.api.data.service.CacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CurrencyServiceImplTest {

    private CacheService cacheService;
    private ExecutorService executorService;
    private CurrencyServiceImpl currencyService;

    @BeforeEach
    void setUp() {
        cacheService = mock(CacheService.class);
        executorService = mock(ExecutorService.class);
        currencyService = new CurrencyServiceImpl(cacheService, executorService);
    }

    @Test
    void testGetAllCurrencies_success() throws Exception {
        Map<String, String> mockData = Map.of("USD", "United States Dollar", "EUR", "Euro");

        // Set up the future to return the mocked data
        Future<Map<String, String>> future = mock(Future.class);
        when(executorService.submit(any(Callable.class))).thenReturn(future);
        when(future.get()).thenReturn(mockData);

        Map<String, String> result = currencyService.getAllCurrencies();

        assertEquals(mockData, result);
        verify(executorService).submit(any(Callable.class));
    }

    @Test
    void testGetAllCurrencies_exception() throws Exception {
        Future<Map<String, String>> future = mock(Future.class);
        when(executorService.submit(any(Callable.class))).thenReturn(future);
        when(future.get()).thenThrow(new ExecutionException(new RuntimeException("Service down")));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> currencyService.getAllCurrencies());

        assertEquals("Failed to fetch currencies", thrown.getMessage());
    }
}
