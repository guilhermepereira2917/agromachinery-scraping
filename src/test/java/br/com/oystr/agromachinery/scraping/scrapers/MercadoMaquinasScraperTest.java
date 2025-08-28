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

class MercadoMaquinasScraperTest {
    private JsoupWrapper jsoupWrapper;
    private MercadoMaquinasScraper mercadoMaquinasScraper;

    @BeforeEach
    void setUp() {
        jsoupWrapper = Mockito.mock(JsoupWrapper.class);
        mercadoMaquinasScraper = new MercadoMaquinasScraper(jsoupWrapper);
    }

    @Test
    void supports_givenUrl_returnsCorrectBoolean() {
        assertTrue(mercadoMaquinasScraper.supports("https://www.mercadomaquinas.com.br/..."));
        assertFalse(mercadoMaquinasScraper.supports("https://www.agrofy.com.br/..."));
    }

    @Test
    void fetch_givenFakeHtml_shouldReturnCorrectMachine() throws Exception {
        final String fakeHtmlFileName = "tests/fake_mercadomaquinas_product.html";
        Document fakeDocument;
        try (var is = getClass().getClassLoader().getResourceAsStream(fakeHtmlFileName)) {
            if (is == null) {
                fail("Test HTML '%s' file not found!".formatted(fakeHtmlFileName));
            }

            fakeDocument = Jsoup.parse(is, StandardCharsets.UTF_8.name(), fakeHtmlFileName);
        }

        when(jsoupWrapper.fetch(anyString())).thenReturn(fakeDocument);

        Machine machine = mercadoMaquinasScraper.fetch("www.mercadomaquinas.com.br/kombi");

        assertNotNull(machine);

        assertEquals("Kombi", machine.getModel());
        assertEquals(ContractType.SALE, machine.getContractType());
        assertEquals("Volkswagen", machine.getMake());
        assertEquals(2020, machine.getYear());
        assertEquals(1200, machine.getWorkedHours());
        assertEquals("Curitiba", machine.getCity());
        assertEquals(new BigDecimal("35000.0"), machine.getPrice());
        assertEquals("https://mercadomaquinas.com.br/kombi.jpg", machine.getPhoto());
        assertEquals("www.mercadomaquinas.com.br/kombi", machine.getUrl());
    }
}