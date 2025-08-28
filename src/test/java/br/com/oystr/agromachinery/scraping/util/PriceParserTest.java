package br.com.oystr.agromachinery.scraping.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PriceParserTest {

    @Test
    void parsePrice_validString_shouldReturnBigDecimal() {
        Optional<BigDecimal> result = PriceParser.parsePrice("R$ 1.234,56");
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("1234.56"), result.get());
    }

    @Test
    void parsePrice_nullOrBlank_shouldReturnEmpty() {
        assertTrue(PriceParser.parsePrice(null).isEmpty());
        assertTrue(PriceParser.parsePrice("").isEmpty());
    }

    @Test
    void parsePrice_invalidString_shouldReturnEmpty() {
        assertTrue(PriceParser.parsePrice("abc").isEmpty());
    }
}