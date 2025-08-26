package br.com.oystr.agromachinery.scraping.scrapers;

import br.com.oystr.agromachinery.scraping.Bot;
import br.com.oystr.agromachinery.scraping.ContractType;
import br.com.oystr.agromachinery.scraping.Machine;
import br.com.oystr.agromachinery.scraping.util.PriceParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
            BigDecimal price = priceString.flatMap(PriceParser::parsePrice).orElse(null);

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
}
