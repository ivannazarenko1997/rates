package com.echange.api.data;




import com.echange.api.data.model.CurrencyDetails;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CurrencyDetailsTest {

    @Test
    public void testConstructorAndGetters() {
        // Arrange
        String description = "United States Dollar";
        String code = "USD";

        // Act
        CurrencyDetails currencyDetails = new CurrencyDetails(description, code);

        // Assert
        assertEquals(description, currencyDetails.getDescription());
        assertEquals(code, currencyDetails.getCode());
    }

    @Test
    public void testSetDescription() {
        // Arrange
        CurrencyDetails currencyDetails = new CurrencyDetails("Euro", "EUR");
        String newDescription = "European Euro";

        // Act
        currencyDetails.setDescription(newDescription);

        // Assert
        assertEquals(newDescription, currencyDetails.getDescription());
    }

    @Test
    public void testSetCode() {
        // Arrange
        CurrencyDetails currencyDetails = new CurrencyDetails("Euro", "EUR");
        String newCode = "EURO";

        // Act
        currencyDetails.setCode(newCode);

        // Assert
        assertEquals(newCode, currencyDetails.getCode());
    }
}