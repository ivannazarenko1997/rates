package com.echange.api.data;



import com.echange.api.data.scheduler.RateSchedulerReader;
import com.echange.api.data.service.CacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RateSchedulerReaderTest {

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private RateSchedulerReader rateSchedulerReader;

    @BeforeEach
    void setUp() {
        // Reset the mock before each test
    }

    @Test
    void testRefreshDataFromUrlWhenCalledThenCacheServiceMethodCalled() {
        // Act
        rateSchedulerReader.refreshDataFromUrl();

        // Assert
        verify(cacheService).refreshDataFromUrl();
    }

    @Test
    void testInitiazileWhenBeanInitializedThenCacheServiceMethodCalled() {
        // Act
        rateSchedulerReader.initiazile();

        // Assert
        verify(cacheService).refreshDataFromUrl();
    }
}