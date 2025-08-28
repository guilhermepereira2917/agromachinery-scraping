package br.com.oystr.agromachinery.scraping.scrapers;

import br.com.oystr.agromachinery.scraping.model.ContractType;
import br.com.oystr.agromachinery.scraping.model.Machine;
import br.com.oystr.agromachinery.scraping.util.ImageConverter;
import br.com.oystr.agromachinery.scraping.util.JsoupWrapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
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
        try (MockedStatic<ImageConverter> mocked = mockStatic(ImageConverter.class)) {
            mocked.when(() -> ImageConverter.convertImageToBase64(anyString())).thenReturn(Optional.of("fakeBase64"));

            Machine machine = mercadoMaquinasScraper.fetch("www.mercadomaquinas.com.br/kombi");

            assertNotNull(machine);

            assertEquals("Kombi", machine.model());
            assertEquals(ContractType.SALE, machine.contractType());
            assertEquals("Volkswagen", machine.make());
            assertEquals(2020, machine.year());
            assertEquals(1200, machine.workedHours());
            assertEquals("Curitiba", machine.city());
            assertEquals(new BigDecimal("35000.0"), machine.price());
            assertEquals("https://mercadomaquinas.com.br/kombi.jpg", machine.photo());
            assertEquals("www.mercadomaquinas.com.br/kombi", machine.url());
        }
    }
}