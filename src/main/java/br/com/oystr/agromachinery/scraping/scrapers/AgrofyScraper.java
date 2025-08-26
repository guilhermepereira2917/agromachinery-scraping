package br.com.oystr.agromachinery.scraping.scrapers;

import br.com.oystr.agromachinery.scraping.ContractType;
import br.com.oystr.agromachinery.scraping.Machine;
import br.com.oystr.agromachinery.scraping.Bot;
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

@Service
public class AgrofyScraper implements Bot {

    private final ObjectMapper objectMapper;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36";
    private static final int TIMEOUT = 10_000;
    private static final Logger log = LoggerFactory.getLogger(AgrofyScraper.class);

    public AgrofyScraper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Machine fetch(String url) {
        try {
            Document document = Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIMEOUT).get();
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
