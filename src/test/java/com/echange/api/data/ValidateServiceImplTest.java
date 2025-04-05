package com.echange.api.data;


import com.echange.api.data.model.CurrencyDetails;
import com.echange.api.data.model.CurrencySymbolsResponse;
import com.echange.api.data.model.ExchangeRateResponse;
import com.echange.api.data.service.impl.ValidateServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ValidateServiceImplTest {

    private final ValidateServiceImpl validateService = new ValidateServiceImpl();

    @Test
    public void testValidateRatesWithNullResponse() {
        assertThrows(RuntimeException.class, () -> validateService.validateRates(null), "response is empty");
    }

    @Test
    public void testValidateRatesWithNullBody() {
        ResponseEntity<ExchangeRateResponse> response = mock(ResponseEntity.class);
        when(response.getBody()).thenReturn(null);

        assertThrows(RuntimeException.class, () -> validateService.validateRates(response), "response is empty");
    }

    @Test
    public void testValidateRatesWithEmptyRates() {
        ExchangeRateResponse exchangeRateResponse = new ExchangeRateResponse("USD", new HashMap<>());
        ResponseEntity<ExchangeRateResponse> response = ResponseEntity.ok(exchangeRateResponse);

        assertThrows(RuntimeException.class, () -> validateService.validateRates(response), "rates is empty");
    }

    @Test
    public void testValidateRatesWithValidRates() {
        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 0.85);
        ExchangeRateResponse exchangeRateResponse = new ExchangeRateResponse("USD", rates);
        ResponseEntity<ExchangeRateResponse> response = ResponseEntity.ok(exchangeRateResponse);

        validateService.validateRates(response);
    }

    @Test
    public void testValidateSymbolsWithNullResponse() {
        assertThrows(RuntimeException.class, () -> validateService.validateSymbols(null), "response is empty");
    }

    @Test
    public void testValidateSymbolsWithNullBody() {
        ResponseEntity<CurrencySymbolsResponse> response = mock(ResponseEntity.class);
        when(response.getBody()).thenReturn(null);

        assertThrows(RuntimeException.class, () -> validateService.validateSymbols(response), "response is empty");
    }

    @Test
    public void testValidateSymbolsWithEmptySymbols() {
        CurrencySymbolsResponse currencySymbolsResponse = new CurrencySymbolsResponse(true, new HashMap<>());
        ResponseEntity<CurrencySymbolsResponse> response = ResponseEntity.ok(currencySymbolsResponse);

        assertThrows(RuntimeException.class, () -> validateService.validateSymbols(response), "symbols is empty");
    }

    @Test
    public void testValidateSymbolsWithValidSymbols() {
        Map<String, CurrencyDetails> symbols = new HashMap<>();
        symbols.put("USD", new CurrencyDetails("United States Dollar", "USD"));
        CurrencySymbolsResponse currencySymbolsResponse = new CurrencySymbolsResponse(true, symbols);
        ResponseEntity<CurrencySymbolsResponse> response = ResponseEntity.ok(currencySymbolsResponse);

        validateService.validateSymbols(response);
    }
}