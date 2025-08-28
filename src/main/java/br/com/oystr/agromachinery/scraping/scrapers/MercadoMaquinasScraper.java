package br.com.oystr.agromachinery.scraping.scrapers;

import br.com.oystr.agromachinery.scraping.bot.Bot;
import br.com.oystr.agromachinery.scraping.exceptions.MachineNotFoundException;
import br.com.oystr.agromachinery.scraping.model.ContractType;
import br.com.oystr.agromachinery.scraping.model.Machine;
import br.com.oystr.agromachinery.scraping.util.JsoupWrapper;
import br.com.oystr.agromachinery.scraping.util.PriceParser;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import static br.com.oystr.agromachinery.scraping.util.ImageConverter.convertImageToBase64;

/**
 * A web scraper implementation for extracting machine details from
 * <a href="https://www.mercadomaquinas.com.br">Mercado Máquinas</a> advertisements.
 * <p>
 * This scraper fetches details from the target page and converts them
 * into a {@link Machine} object.
 * </p>
 *
 * <p>It uses {@link Jsoup} for HTML parsing.</p>
 *
 * <p>Implements {@link Bot} to provide a uniform interface for fetching and determining
 * support for URLs.</p>
 */
@Service
public class MercadoMaquinasScraper implements Bot {

    private final JsoupWrapper jsoupWrapper;
    private static final Logger log = LoggerFactory.getLogger(MercadoMaquinasScraper.class);

    public MercadoMaquinasScraper(JsoupWrapper jsoupWrapper) {
        this.jsoupWrapper = jsoupWrapper;
    }

    @Override
    public Machine fetch(String url) {
        try {
            Document document = fetchDocument(url);

            String model = Optional.ofNullable(document.selectFirst("h1.title")).map(Element::text).orElse(null);
            ContractType contractType = ContractType.SALE;
            String make = findProductInfo(document, "Fabricante").orElse(null);
            Integer year = findProductInfo(document, "Ano").map(Integer::valueOf).orElse(null);

            Integer workedHours = Optional.ofNullable(document.selectFirst("li.item.spec:has(span.name:containsOwn(Horas trabalhadas:)) span.value"))
                .map(Element::text)
                .map(s -> s.replace(" ", "").replace("h", "").replace(".", ""))
                .map(Integer::valueOf)
                .orElse(null);

            String city = findProductInfo(document, "Localização").orElse(null);

            BigDecimal price = Optional.ofNullable(document.selectFirst("div.price span.value"))
                .map(Element::text)
                .flatMap(PriceParser::parsePrice)
                .orElse(null);

            String photo = Optional.ofNullable(document.selectFirst("#ad-main-photo img"))
                .map(e -> e.attr("abs:src"))
                .orElse(null);

            String photoBase64 = convertImageToBase64(photo).orElse(null);

            return new Machine(model, contractType, make, year, workedHours, city, price, photo, photoBase64, url);
        } catch (Exception e) {
            log.error("Error while processing URL {}", url, e);

            return null;
        }
    }

    @Override
    public boolean supports(String url) {
        return url.contains("mercadomaquinas.com.br");
    }

    private Optional<String> findProductInfo(Document document, String label) {
        String cssSelector = "li.item:has(span.item-name:containsOwn(%s:)) span.item-value".formatted(label);

        return Optional.ofNullable(document.selectFirst(cssSelector)).map(Element::text);
    }

    private Document fetchDocument(String url) throws IOException {
        try {
            return jsoupWrapper.fetch(url);
        } catch (HttpStatusException e) {
            if (e.getStatusCode() == 404) {
                throw new MachineNotFoundException("Machine not found on URL: " + url);
            }

            throw e;
        }
    }
}
