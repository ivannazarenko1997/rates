package com.echange.api.data;


import com.echange.api.data.model.ExchangeRateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ExchangeRateResponseTest {

    private ExchangeRateResponse exchangeRateResponse;
    private Map<String, Double> rates;

    @BeforeEach
    public void setUp() {
        rates = new HashMap<>();
        rates.put("USD", 1.0);
        rates.put("EUR", 0.85);
        exchangeRateResponse = new ExchangeRateResponse("USD", rates);
    }

    @Test
    public void testDefaultConstructor() {
        ExchangeRateResponse defaultResponse = new ExchangeRateResponse();
        assertNull(defaultResponse.getBase());
        assertNull(defaultResponse.getRates());
    }

    @Test
    public void testParameterizedConstructor() {
        assertEquals("USD", exchangeRateResponse.getBase());
        assertEquals(rates, exchangeRateResponse.getRates());
    }

    @Test
    public void testSetBase() {
        exchangeRateResponse.setBase("EUR");
        assertEquals("EUR", exchangeRateResponse.getBase());
    }

    @Test
    public void testSetRates() {
        Map<String, Double> newRates = new HashMap<>();
        newRates.put("GBP", 0.75);
        exchangeRateResponse.setRates(newRates);
        assertEquals(newRates, exchangeRateResponse.getRates());
    }

    @Test
    public void testGetBase() {
        assertEquals("USD", exchangeRateResponse.getBase());
    }

    @Test
    public void testGetRates() {
        assertEquals(rates, exchangeRateResponse.getRates());
    }
}