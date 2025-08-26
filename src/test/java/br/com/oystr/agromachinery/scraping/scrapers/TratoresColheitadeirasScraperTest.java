package br.com.oystr.agromachinery.scraping.scrapers;

import br.com.oystr.agromachinery.scraping.ContractType;
import br.com.oystr.agromachinery.scraping.Machine;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

class TratoresColheitadeirasScraperTest {
    private TratoresColheitadeirasScraper tratoresColheitadeirasScraper;

    @BeforeEach
    void setUp() {
        ScraperProperties props = new ScraperProperties();
        props.setUserAgent("Fake-UA");
        props.setTimeout(5000);

        tratoresColheitadeirasScraper = new TratoresColheitadeirasScraper(props);
    }

    @Test
    void testSupports() {
        assertTrue(tratoresColheitadeirasScraper.supports("https://www.tratoresecolheitadeiras.com.br/..."));
        assertFalse(tratoresColheitadeirasScraper.supports("https://www.agrofy.com.br/..."));
    }

    /**
     * Tests the fetch method using a fake HTML file.
     * Ensures all fields of {@link Machine} are parsed correctly.
     */
    @Test
    void testFetch() throws Exception {
        final String fakeHtmlFileName = "tests/fake_tratoresecolheitadeiras_product.html";
        Document fakeDocument;
        try (var is = getClass().getClassLoader().getResourceAsStream(fakeHtmlFileName)) {
            if (is == null) {
                fail("Test HTML '%s' file not found!".formatted(fakeHtmlFileName));
            }

            fakeDocument = Jsoup.parse(is, StandardCharsets.UTF_8.name(), fakeHtmlFileName);
        }

        try (var jsoupMock = Mockito.mockStatic(Jsoup.class)) {
            Connection mockConnection = mock(Connection.class);
            jsoupMock.when(() -> Jsoup.connect(anyString())).thenReturn(mockConnection);

            when(mockConnection.userAgent(anyString())).thenReturn(mockConnection);
            when(mockConnection.timeout(anyInt())).thenReturn(mockConnection);
            when(mockConnection.get()).thenReturn(fakeDocument);

            Machine machine = tratoresColheitadeirasScraper.fetch("www.tratoresecolheitadeiras.com.br/colheitadeira");

            assertNotNull(machine);

            assertEquals("Colheitadeira Modelo X", machine.getModel());
            assertEquals(ContractType.SALE, machine.getContractType());
            assertEquals("John Deere", machine.getMake());
            assertEquals(2023, machine.getYear());
            assertEquals(120, machine.getWorkedHours());
            assertEquals("Erechim/RS", machine.getCity());
            assertEquals(new BigDecimal("123456.78"), machine.getPrice());
            assertEquals("https://example.com/fake-image.jpg", machine.getPhoto());
            assertEquals("www.tratoresecolheitadeiras.com.br/colheitadeira", machine.getUrl());
        }
    }
}
