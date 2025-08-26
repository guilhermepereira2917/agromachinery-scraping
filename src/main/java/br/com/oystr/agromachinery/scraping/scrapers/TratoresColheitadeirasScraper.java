package br.com.oystr.agromachinery.scraping.scrapers;

import br.com.oystr.agromachinery.scraping.Bot;
import br.com.oystr.agromachinery.scraping.ContractType;
import br.com.oystr.agromachinery.scraping.Machine;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Optional;


@Service
public class TratoresColheitadeirasScraper implements Bot {

    private final ScraperProperties scraperProperties;
    private static final Logger log = LoggerFactory.getLogger(TratoresColheitadeirasScraper.class);

    public TratoresColheitadeirasScraper(ScraperProperties scraperProperties) {
        this.scraperProperties = scraperProperties;
    }

    @Override
    public Machine fetch(String url) {
        try {
            Document document = Jsoup.connect(url).userAgent(scraperProperties.getUserAgent()).timeout(scraperProperties.getTimeout()).get();

            String model = Optional.ofNullable(document.selectFirst(".product-single__title")).map(Element::text).orElse(null);
            ContractType contractType = ContractType.SALE;
            String make = findDetail(document, "Marca").orElse(null);
            Integer year = findDetail(document, "Ano de Fabricação").map(Integer::parseInt).orElse(null);
            Integer workedHours = findDetail(document, "Horas").map(Integer::parseInt).orElse(null);

            Optional<Element> addressNode = Optional.ofNullable(document.select(".product-single__description.rte li").last());
            String city = addressNode.map(Element::lastChild).map(Node::nodeValue).map(String::trim).orElse(null);

            Optional<String> priceString = findDetail(document, "Preço");
            BigDecimal price = priceString.flatMap(this::parsePrice).orElse(null);

            String photo = Optional.ofNullable(document.selectFirst("[data-image]")).map(e -> e.attribute("data-image")).map(Attribute::getValue).orElse(null);

            return new Machine(model, contractType, make, year, workedHours, city, price, photo, url);
        } catch (Exception e) {
            log.error("Error while processing URL {}", url, e);

            return null;
        }
    }

    @Override
    public boolean supports(String url) {
        return url.contains("tratoresecolheitadeiras.com.br");
    }

    private Optional<String> findDetail(Document document, String label) {
        return Optional.ofNullable(document.selectFirst("p:containsOwn(" + label + ") strong")).map(Element::text);
    }

    private Optional<BigDecimal> parsePrice(String priceString) {
        if (priceString == null || priceString.isBlank()) {
            return Optional.empty();
        }

        NumberFormat format = NumberFormat.getInstance(Locale.of("pt", "BR"));
        String cleanedPriceString = priceString.replace("R$ ", "").replace(" ", "");

        try {
            Number number = format.parse(cleanedPriceString);
            return Optional.of(BigDecimal.valueOf(number.doubleValue()));
        } catch (ParseException e) {
            return Optional.empty();
        }
    }
}
