package br.com.oystr.agromachinery.scraping.service.scrapers;

import br.com.oystr.agromachinery.scraping.exceptions.MachineNotFoundException;
import br.com.oystr.agromachinery.scraping.model.ContractType;
import br.com.oystr.agromachinery.scraping.model.Machine;
import br.com.oystr.agromachinery.scraping.service.scrapers.AgrofyScraper;
import br.com.oystr.agromachinery.scraping.util.ImageConverter;
import br.com.oystr.agromachinery.scraping.util.JsoupWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    void supports_givenUrl_returnsCorrectBoolean() {
        assertTrue(agrofyScraper.supports("https://www.agrofy.com.br/..."));
        assertFalse(agrofyScraper.supports("https://www.mercadomaquinas.com.br/..."));
    }

    @Test
    void fetch_givenMockHtml_shouldReturnCorrectMachine() throws Exception {
        Document mockHtml = loadDocument("mock_agrofy_product.html");
        when(jsoupWrapper.fetch("https://www.agrofy.com.br/tractor")).thenReturn(mockHtml);

        try (MockedStatic<ImageConverter> mocked = mockStatic(ImageConverter.class)) {
            mocked.when(() -> ImageConverter.convertImageToBase64(anyString())).thenReturn(Optional.of("mockBase64"));

            Machine machine = agrofyScraper.fetch("https://www.agrofy.com.br/tractor");

            assertNotNull(machine);
            assertEquals("Trator Magnum", machine.model());
            assertEquals(ContractType.SALE, machine.contractType());
            assertEquals("John Deere", machine.make());
            assertEquals(2022, machine.year());
            assertEquals(120, machine.workedHours());
            assertEquals("Erechim", machine.city());
            assertEquals(new BigDecimal("12345.67"), machine.price());
            assertEquals("url.jpg", machine.photo());
            assertEquals("https://www.agrofy.com.br/tractor", machine.url());
        }
    }

    @Test
    @ExtendWith(OutputCaptureExtension.class)
    void fetch_givenMockNonListedHtml_shouldReturnNull(CapturedOutput output) throws Exception {
        Document mockHtml = loadDocument("mock_agrofy_product_nonlisted.html");
        when(jsoupWrapper.fetch("https://www.agrofy.com.br/tractor")).thenReturn(mockHtml);

        Machine machine = agrofyScraper.fetch("https://www.agrofy.com.br/tractor");

        assertNull(machine);
        assertTrue(output.getAll().contains(MachineNotFoundException.class.getName()));
    }
}