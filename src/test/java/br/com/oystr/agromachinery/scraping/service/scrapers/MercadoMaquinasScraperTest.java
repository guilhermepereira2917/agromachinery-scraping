package br.com.oystr.agromachinery.scraping.service.scrapers;

import br.com.oystr.agromachinery.scraping.exceptions.MachineNotFoundException;
import br.com.oystr.agromachinery.scraping.model.ContractType;
import br.com.oystr.agromachinery.scraping.model.Machine;
import br.com.oystr.agromachinery.scraping.service.scrapers.MercadoMaquinasScraper;
import br.com.oystr.agromachinery.scraping.util.ImageConverter;
import br.com.oystr.agromachinery.scraping.util.JsoupWrapper;
import org.jsoup.HttpStatusException;
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
    void fetch_givenMockHtml_shouldReturnCorrectMachine() throws Exception {
        Document mockHtml = loadDocument("mock_mercadomaquinas_product.html");
        when(jsoupWrapper.fetch(anyString())).thenReturn(mockHtml);

        try (MockedStatic<ImageConverter> mocked = mockStatic(ImageConverter.class)) {
            mocked.when(() -> ImageConverter.convertImageToBase64(anyString())).thenReturn(Optional.of("mockBase64"));

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

    @Test
    @ExtendWith(OutputCaptureExtension.class)
    void fetch_givenHttpStatusError404_shouldReturnNull(CapturedOutput output) throws Exception {
        final String fetchUrl = "www.mercadomaquinas.com.br/kombi";

        when(jsoupWrapper.fetch(anyString()))
            .thenThrow(new HttpStatusException("Not Found 404", 404, fetchUrl));

        Machine machine = mercadoMaquinasScraper.fetch(fetchUrl);

        assertNull(machine);
        assertTrue(output.getAll().contains(MachineNotFoundException.class.getName()));
    }
}