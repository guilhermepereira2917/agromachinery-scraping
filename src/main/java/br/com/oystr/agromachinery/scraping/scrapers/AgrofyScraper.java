package br.com.oystr.agromachinery.scraping.scrapers;

import br.com.oystr.agromachinery.scraping.ContractType;
import br.com.oystr.agromachinery.scraping.Machine;
import br.com.oystr.agromachinery.scraping.Bot;
import br.com.oystr.agromachinery.scraping.util.JsoupWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Optional;

/**
 * Scraper implementation for <a href="https://www.agrofy.com.br">Agrofy</a> machinery listings.
 *
 * <p>This scraper behaves differently than traditional HTML scrapers because Agrofy uses a
 * Next.js frontend. When fetching the page with Jsoup, the initial HTML does not contain the
 * fully rendered content because Next.js hydrates the page on the client side. Therefore, instead
 * of scraping the HTML directly, this scraper locates the JSON data embedded inside the
 * {@code <script id="__NEXT_DATA__">} tag, which contains all the product details needed to
 * construct a {@link Machine} object.</p>
 *
 * <p>Implements {@link Bot} to provide a uniform interface for fetching and determining
 * support for URLs.</p>
 */
@Service
public class AgrofyScraper implements Bot {

    private final JsoupWrapper jsoupWrapper;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(AgrofyScraper.class);

    public AgrofyScraper(JsoupWrapper jsoupWrapper, ObjectMapper objectMapper) {
        this.jsoupWrapper = jsoupWrapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public Machine fetch(String url) {
        try {
            Document document = jsoupWrapper.fetch(url);
            Element scriptTag = document.selectFirst("script#__NEXT_DATA__");
            String json = scriptTag.html();

            JsonNode root = objectMapper.readTree(json);
            JsonNode productData = root.path("props").path("pageProps").path("productData");
            JsonNode product = productData.path("product");
            JsonNode productDetails = productData.path("productDetails");

            String model = product.path("title").asText();
            ContractType contractType = parseContractType(product.path("finalidad").asText());
            String make = product.path("marca").asText();
            Integer year = extractIntAttribute(productDetails, "ano_fabricacion");
            Integer workedHours = extractIntAttribute(productDetails, "cn2_uso");
            String city = product.path("localizacion").asText();
            BigDecimal price = BigDecimal.valueOf(product.path("price").asDouble());
            String photo = extractFirstImage(product);

            return new Machine(model, contractType, make, year, workedHours, city, price, photo, url);
        } catch (Exception e) {
            log.error("Error while processing URL {}", url, e);

            return null;
        }
    }

    @Override
    public boolean supports(String url) {
        return url.contains("agrofy.com.br");
    }

    private ContractType parseContractType(String type) {
        return "Venda".equalsIgnoreCase(type) ? ContractType.SALE : ContractType.RENT;
    }

    private Optional<JsonNode> findAttribute(JsonNode productDetails, String attributeCode) {
        return productDetails.valueStream()
            .filter(node -> attributeCode.equals(node.path("attributeCode").asText()))
            .findFirst();
    }

    private Integer extractIntAttribute(JsonNode productDetails, String attributeCode) {
        return findAttribute(productDetails, attributeCode)
            .map(n -> n.path("value").asInt())
            .orElse(null);
    }

    private String extractFirstImage(JsonNode product) {
        Iterator<JsonNode> images = product.path("images").elements();
        return images.hasNext() ? images.next().path("image").asText() : null;
    }
}
