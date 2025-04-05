package com.echange.api.data;


import com.echange.api.data.model.CachedRates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CachedRatesTest {

    private Map<String, Double> rates;
    private CachedRates cachedRates;

    @BeforeEach
    public void setUp() {
        rates = new HashMap<>();
        rates.put("USD", 1.0);
        rates.put("EUR", 0.85);
        cachedRates = new CachedRates(rates);
    }

    @Test
    public void testGetRates() {
        Map<String, Double> retrievedRates = cachedRates.getRates();
        assertNotNull(retrievedRates);
        assertEquals(2, retrievedRates.size());
        assertEquals(1.0, retrievedRates.get("USD"));
        assertEquals(0.85, retrievedRates.get("EUR"));
    }

    @Test
    public void testIsExpiredWhenNotExpired() throws InterruptedException {
        // Assuming the expiration time is 60 seconds, we check immediately
        assertFalse(cachedRates.isExpired());
    }

    @Test
    public void testIsExpiredWhenExpired() throws InterruptedException {
        // Simulate waiting for more than 60 seconds
        Thread.sleep(61_000);
        assertTrue(cachedRates.isExpired());
    }
}