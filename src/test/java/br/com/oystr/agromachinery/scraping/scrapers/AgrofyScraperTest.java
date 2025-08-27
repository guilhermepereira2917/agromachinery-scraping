package br.com.oystr.agromachinery.scraping.scrapers;

import br.com.oystr.agromachinery.scraping.ContractType;
import br.com.oystr.agromachinery.scraping.Machine;
import br.com.oystr.agromachinery.scraping.util.JsoupWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AgrofyScraper}.
 *
 * <p>
 * Tests the scraping logic for Agrofy product pages, including URL support
 * detection and parsing of product JSON data. Uses Mockito to mock Jsoup
 * HTTP calls and provide a fake JSON payload.
 * </p>
 */
class AgrofyScraperTest {
    private JsoupWrapper jsoupWrapper;
    private AgrofyScraper agrofyScraper;

    @BeforeEach
    void setUp() {
        jsoupWrapper = Mockito.mock(JsoupWrapper.class);
        ObjectMapper objectMapper = new ObjectMapper();

        agrofyScraper = new AgrofyScraper(jsoupWrapper, objectMapper);
    }

    @Test
    void testSupports() {
        assertTrue(agrofyScraper.supports("https://www.agrofy.com.br/..."));
        assertFalse(agrofyScraper.supports("https://www.mercadomaquinas.com.br/..."));
    }

    /**
     * Tests the fetch method using a fake JSON file.
     * Ensures all fields of {@link Machine} are parsed correctly.
     */
    @Test
    void testFetch() throws Exception {
        final String fakeJsonFileName = "tests/fake_agrofy_product.json";
        String mockJson;
        try (var is = getClass().getClassLoader().getResourceAsStream(fakeJsonFileName)) {
            if (is == null) {
                fail("Test JSON '%s' file not found!".formatted(fakeJsonFileName));
            }

            mockJson = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }

        Document mockDocument = Mockito.mock(Document.class);
        Element mockScript = Mockito.mock(Element.class);

        when(jsoupWrapper.fetch("https://www.agrofy.com.br/tractor")).thenReturn(mockDocument);
        when(mockDocument.selectFirst("script#__NEXT_DATA__")).thenReturn(mockScript);
        when(mockScript.html()).thenReturn(mockJson);

        Machine machine = agrofyScraper.fetch("https://www.agrofy.com.br/tractor");

        assertNotNull(machine);
        assertEquals("Trator Magnum", machine.getModel());
        assertEquals(ContractType.SALE, machine.getContractType());
        assertEquals("John Deere", machine.getMake());
        assertEquals(2022, machine.getYear());
        assertEquals(120, machine.getWorkedHours());
        assertEquals("Erechim", machine.getCity());
        assertEquals(new BigDecimal("12345.67"), machine.getPrice());
        assertEquals("url.jpg", machine.getPhoto());
        assertEquals("https://www.agrofy.com.br/tractor", machine.getUrl());
    }
}