package br.com.oystr.agromachinery.scraping.scrapers;

import br.com.oystr.agromachinery.scraping.exceptions.MachineNotFoundException;
import br.com.oystr.agromachinery.scraping.model.ContractType;
import br.com.oystr.agromachinery.scraping.model.Machine;
import br.com.oystr.agromachinery.scraping.util.ImageConverter;
import br.com.oystr.agromachinery.scraping.util.JsoupWrapper;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static br.com.oystr.agromachinery.scraping.testutils.TestHtmlFileLoader.loadDocument;
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
        Document mockHtml = loadDocument("mock_tratoresecolheitadeiras_product.html");
        when(jsoupWrapper.fetch(anyString())).thenReturn(mockHtml);

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

    @Test
    @ExtendWith(OutputCaptureExtension.class)
    void fetch_givenMockNonListedHtml_shouldReturnNull(CapturedOutput output) throws Exception {
        Document mockHtml = loadDocument("mock_tratoresecolheitadeiras_product_nonlisted.html");
        when(jsoupWrapper.fetch("www.tratoresecolheitadeiras.com.br/colheitadeira")).thenReturn(mockHtml);

        Machine machine = tratoresColheitadeirasScraper.fetch("www.tratoresecolheitadeiras.com.br/colheitadeira");

        assertNull(machine);
        assertTrue(output.getAll().contains(MachineNotFoundException.class.getName()));
    }
}
