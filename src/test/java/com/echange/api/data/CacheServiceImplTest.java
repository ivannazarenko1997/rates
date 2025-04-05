package com.echange.api.data;

import com.echange.api.data.model.CachedRates;
import com.echange.api.data.service.RestAPICallService;
import com.echange.api.data.service.impl.CacheServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CacheServiceImplTest {

    @Mock
    private RestAPICallService restAPICallService;

    private ExecutorService virtualThreadExecutor;

    @InjectMocks
    private CacheServiceImpl cacheService;

    @BeforeEach
    public void setUp() {
        virtualThreadExecutor = Executors.newSingleThreadExecutor();
        cacheService = new CacheServiceImpl(restAPICallService, virtualThreadExecutor);
    }

    @Test
    public void testGetRatesFromCacheWhenCacheIsEmptyThenFetchFromAPI() throws Exception {
        // Arrange
        Map<String, Double> expectedRates = Collections.singletonMap("USD", 1.0);
        when(restAPICallService.getRates(anyString())).thenReturn(expectedRates);

        // Act
        Map<String, Double> actualRates = cacheService.getRatesFromCache("USD");

        // Assert
        verify(restAPICallService, times(1)).getRates("USD");
        assertThat(actualRates).isEqualTo(expectedRates);
    }

    @Test
    public void testGetRatesFromCacheWhenCacheIsNotEmptyThenFetchFromCache() throws Exception {
        // Arrange
        Map<String, Double> expectedRates = Collections.singletonMap("USD", 1.0);
        CachedRates cachedRates = new CachedRates(expectedRates);
        cacheService.putCache("USD", cachedRates);

        // Act
        Map<String, Double> actualRates = cacheService.getRatesFromCache("USD");

        // Assert
        verify(restAPICallService, never()).getRates(anyString());
        assertThat(actualRates).isEqualTo(expectedRates);
    }

    @Test
    public void testGetRatesFromCacheWhenCacheIsExpiredThenFetchFromAPI() throws Exception {
        // Arrange
        Map<String, Double> expiredRates = Collections.singletonMap("USD", 1.0);
        CachedRates expiredCachedRates = new CachedRates(expiredRates) {
            @Override
            public boolean isExpired() {
                return true;
            }
        };
        cacheService.putCache("USD", expiredCachedRates);

        Map<String, Double> newRates = Collections.singletonMap("USD", 1.1);
        when(restAPICallService.getRates(anyString())).thenReturn(newRates);

        // Act
        Map<String, Double> actualRates = cacheService.getRatesFromCache("USD");

        // Assert
        verify(restAPICallService, times(1)).getRates("USD");
        assertThat(actualRates).isEqualTo(newRates);
    }
}