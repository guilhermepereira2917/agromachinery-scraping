package br.com.oystr.agromachinery.scraping.scrapers;

import br.com.oystr.agromachinery.scraping.Bot;
import br.com.oystr.agromachinery.scraping.ContractType;
import br.com.oystr.agromachinery.scraping.Machine;
import br.com.oystr.agromachinery.scraping.util.PriceParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class MercadoMaquinasScraper implements Bot {

    private final ScraperProperties scraperProperties;
    private static final Logger log = LoggerFactory.getLogger(MercadoMaquinasScraper.class);

    public MercadoMaquinasScraper(ScraperProperties scraperProperties) {
        this.scraperProperties = scraperProperties;
    }

    @Override
    public Machine fetch(String url) {
        try {
            Document document = Jsoup.connect(url)
                .userAgent(scraperProperties.getUserAgent())
                .timeout(scraperProperties.getTimeout())
                .get();

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

            return new Machine(model, contractType, make, year, workedHours, city, price, photo, url);
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
}
