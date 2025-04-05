package com.echange.api.data;


import com.echange.api.data.model.CurrencyDetails;
import com.echange.api.data.model.CurrencySymbolsResponse;
import com.echange.api.data.model.ExchangeRateResponse;
import com.echange.api.data.service.ValidateService;
import com.echange.api.data.service.impl.RestAPICallServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RestAPICallServiceImplTest {

    @Mock
    private ValidateService validateService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RetryTemplate retryTemplate;

    @Mock
    private ExecutorService virtualThreadExecutor;

    @InjectMocks
    private RestAPICallServiceImpl restAPICallServiceImpl;

    @Mock
    private Future<ExchangeRateResponse> exchangeRateFuture;

    @Mock
    private Future<CurrencySymbolsResponse> currencySymbolsFuture;

    @BeforeEach
    public void setUp() {
        restAPICallServiceImpl = new RestAPICallServiceImpl(validateService, restTemplate, retryTemplate, virtualThreadExecutor);
    }

    @Test
    public void testGetExchangeRateRestWhenApiCallSuccessful() throws Exception {
        // Arrange
        ExchangeRateResponse expectedResponse = new ExchangeRateResponse("USD", new HashMap<>());
        when(virtualThreadExecutor.submit(any(Callable.class))).thenReturn(exchangeRateFuture);
        when(exchangeRateFuture.get()).thenReturn(expectedResponse);

        // Act
        ExchangeRateResponse actualResponse = restAPICallServiceImpl.getExchangeRateRest("USD");

        // Assert
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testGetExchangeRateRestWhenApiCallFails() throws Exception {
        // Arrange
        when(virtualThreadExecutor.submit(any(Callable.class))).thenReturn(exchangeRateFuture);
        when(exchangeRateFuture.get()).thenThrow(new RuntimeException("API call failed"));

        // Act
        ExchangeRateResponse actualResponse = restAPICallServiceImpl.getExchangeRateRest("USD");

        // Assert
        assertNull(actualResponse);
    }



    @Test
    public void testGetAllCurrenciesRestWhenApiCallSuccessful() throws Exception {
        // Arrange
        CurrencySymbolsResponse expectedResponse = new CurrencySymbolsResponse(true, new HashMap<>());
        when(virtualThreadExecutor.submit(any(Callable.class))).thenReturn(currencySymbolsFuture);
        when(currencySymbolsFuture.get()).thenReturn(expectedResponse);

        // Act
        CurrencySymbolsResponse actualResponse = restAPICallServiceImpl.getAllCurrenciesRest();

        // Assert
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testGetAllCurrenciesRestWhenApiCallFails() throws Exception {
        // Arrange
        when(virtualThreadExecutor.submit(any(Callable.class))).thenReturn(currencySymbolsFuture);
        when(currencySymbolsFuture.get()).thenThrow(new RuntimeException("API call failed"));

        // Act
        CurrencySymbolsResponse actualResponse = restAPICallServiceImpl.getAllCurrenciesRest();

        // Assert
        assertNull(actualResponse);
    }




}