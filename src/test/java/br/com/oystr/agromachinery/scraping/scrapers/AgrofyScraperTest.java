package br.com.oystr.agromachinery.scraping.scrapers;

import br.com.oystr.agromachinery.scraping.ContractType;
import br.com.oystr.agromachinery.scraping.Machine;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
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
    private AgrofyScraper agrofyScraper;

    @BeforeEach
    void setUp() {
        ScraperProperties props = new ScraperProperties();
        props.setUserAgent("Fake-UA");
        props.setTimeout(5000);

        ObjectMapper objectMapper = new ObjectMapper();

        agrofyScraper = new AgrofyScraper(props, objectMapper);
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
        Document mockDocument = mock(Document.class);
        Element mockScript = mock(Element.class);
        when(mockDocument.selectFirst("script#__NEXT_DATA__")).thenReturn(mockScript);

        final String fakeJsonFileName = "tests/fake_agrofy_product.json";
        String fakeJson;
        try (var is = getClass().getClassLoader().getResourceAsStream(fakeJsonFileName)) {
            if (is == null) {
                fail("Test JSON '%s' file not found!".formatted(fakeJsonFileName));
            }

            fakeJson = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }

        when(mockScript.html()).thenReturn(fakeJson);

        try (var mocked = Mockito.mockStatic(Jsoup.class)) {
            Connection mockConnection = mock(Connection.class);
            mocked.when(() -> Jsoup.connect(anyString())).thenReturn(mockConnection);
            when(mockConnection.userAgent(anyString())).thenReturn(mockConnection);
            when(mockConnection.timeout(anyInt())).thenReturn(mockConnection);
            when(mockConnection.get()).thenReturn(mockDocument);

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
}