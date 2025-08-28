package br.com.oystr.agromachinery.scraping.scrapers;

import br.com.oystr.agromachinery.scraping.model.ContractType;
import br.com.oystr.agromachinery.scraping.model.Machine;
import br.com.oystr.agromachinery.scraping.util.JsoupWrapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class TratoresColheitadeirasScraperTest {
    private JsoupWrapper jsoupWrapper;
    private TratoresColheitadeirasScraper tratoresColheitadeirasScraper;

    @BeforeEach
    void setUp() {
        jsoupWrapper = Mockito.mock(JsoupWrapper.class);
        tratoresColheitadeirasScraper = new TratoresColheitadeirasScraper(jsoupWrapper);
    }

    @Test
    void supports_givenUrl_returnsCorrectBoolean() {
        assertTrue(tratoresColheitadeirasScraper.supports("https://www.tratoresecolheitadeiras.com.br/..."));
        assertFalse(tratoresColheitadeirasScraper.supports("https://www.agrofy.com.br/..."));
    }

    @Test
    void fetch_givenFakeHtml_shouldReturnCorrectMachine() throws Exception {
        final String fakeHtmlFileName = "tests/fake_tratoresecolheitadeiras_product.html";
        Document fakeDocument;
        try (var is = getClass().getClassLoader().getResourceAsStream(fakeHtmlFileName)) {
            if (is == null) {
                fail("Test HTML '%s' file not found!".formatted(fakeHtmlFileName));
            }

            fakeDocument = Jsoup.parse(is, StandardCharsets.UTF_8.name(), fakeHtmlFileName);
        }

        when(jsoupWrapper.fetch(anyString())).thenReturn(fakeDocument);

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
