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
    void fetch_givenMockHtml_shouldReturnCorrectMachine() throws Exception {
        final String mockHtmlFileName = "tests/mock_tratoresecolheitadeiras_product.html";
        Document mockDocument;
        try (var is = getClass().getClassLoader().getResourceAsStream(mockHtmlFileName)) {
            if (is == null) {
                fail("Test HTML '%s' file not found!".formatted(mockHtmlFileName));
            }

            mockDocument = Jsoup.parse(is, StandardCharsets.UTF_8.name(), mockHtmlFileName);
        }

        when(jsoupWrapper.fetch(anyString())).thenReturn(mockDocument);
        try (MockedStatic<ImageConverter> mocked = mockStatic(ImageConverter.class)) {
            mocked.when(() -> ImageConverter.convertImageToBase64(anyString())).thenReturn(Optional.of("mockBase64"));

            Machine machine = tratoresColheitadeirasScraper.fetch("www.tratoresecolheitadeiras.com.br/colheitadeira");

            assertNotNull(machine);

            assertEquals("Colheitadeira Modelo X", machine.model());
            assertEquals(ContractType.SALE, machine.contractType());
            assertEquals("John Deere", machine.make());
            assertEquals(2023, machine.year());
            assertEquals(120, machine.workedHours());
            assertEquals("Erechim/RS", machine.city());
            assertEquals(new BigDecimal("123456.78"), machine.price());
            assertEquals("https://example.com/mock-image.jpg", machine.photo());
            assertEquals("www.tratoresecolheitadeiras.com.br/colheitadeira", machine.url());
        }
    }
}
