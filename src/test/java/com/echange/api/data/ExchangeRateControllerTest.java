package com.echange.api.data;

import com.echange.api.data.controller.ExchangeRateController;
import com.echange.api.data.service.CurrencyService;
import com.echange.api.data.service.ExchangeRateRetrieveService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebMvcTest(ExchangeRateController.class)
public class ExchangeRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateRetrieveService exchangeRateRetrieveService;

    @MockBean
    private CurrencyService currencyService;

    @Test
    public void testGetRate() throws Exception {
        String from = "USD";
        String to = "EUR";
        double expectedRate = 0.85;

        Mockito.when(exchangeRateRetrieveService.getExchangeRate(from, to)).thenReturn(expectedRate);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/api/rate")
                        .param("from", from)
                        .param("to", to))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(String.valueOf(expectedRate)));
    }

    @Test
    public void testGetAllRates() throws Exception {
        String from = "USD";
        Map<String, Double> expectedRates = new HashMap<>();
        expectedRates.put("EUR", 0.85);
        expectedRates.put("GBP", 0.75);

        Mockito.when(exchangeRateRetrieveService.getAllExchangeRates(from)).thenReturn(expectedRates);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/api/rates")
                        .param("from", from))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.EUR").value(0.85))
                .andExpect(MockMvcResultMatchers.jsonPath("$.GBP").value(0.75));
    }

    @Test
    public void testConvert() throws Exception {
        String from = "USD";
        String to = "EUR";
        double amount = 100;
        double expectedConvertedAmount = 85;

        Mockito.when(exchangeRateRetrieveService.convert(from, to, amount)).thenReturn(expectedConvertedAmount);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/api/convert")
                        .param("from", from)
                        .param("to", to)
                        .param("amount", String.valueOf(amount)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(String.valueOf(expectedConvertedAmount)));
    }

    @Test
    public void testConvertMultiple() throws Exception {
        String from = "USD";
        double amount = 100;
        List<String> toCurrencies = List.of("EUR", "GBP");
        Map<String, Double> expectedConvertedAmounts = new HashMap<>();
        expectedConvertedAmounts.put("EUR", 85.0);
        expectedConvertedAmounts.put("GBP", 75.0);

        Mockito.when(exchangeRateRetrieveService.convertToMultiple(from, amount, toCurrencies)).thenReturn(expectedConvertedAmounts);

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/api/convert-multiple")
                        .param("from", from)
                        .param("amount", String.valueOf(amount))
                        .content("[\"EUR\",\"GBP\"]")
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.EUR").value(85.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.GBP").value(75.0));
    }

    @Test
    public void testCurrencyAll() throws Exception {
        Map<String, String> expectedCurrencies = new HashMap<>();
        expectedCurrencies.put("USD", "United States Dollar");
        expectedCurrencies.put("EUR", "Euro");

        Mockito.when(currencyService.getAllCurrencies()).thenReturn(expectedCurrencies);

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/api/currency/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.USD").value("United States Dollar"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.EUR").value("Euro"));
    }
}