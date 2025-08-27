package br.com.oystr.agromachinery.scraping.util;

import br.com.oystr.agromachinery.scraping.scrapers.ScraperProperties;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * A utility service that wraps {@link Jsoup} HTTP calls for fetching and parsing HTML documents.
 * <p>
 * This wrapper centralizes common settings such as the User-Agent header and connection timeout,
 * allowing scrapers to reuse a consistent configuration for HTTP requests.
 * </p>
 */
@Service
public class JsoupWrapper {
    private final ScraperProperties scraperProperties;

    public JsoupWrapper(ScraperProperties scraperProperties) {
        this.scraperProperties = scraperProperties;
    }

    /**
     * Fetches and parses an HTML document from the given URL using {@link Jsoup}.
     * <p>
     * The request uses the default User-Agent and timeout configured in
     * {@link ScraperProperties}. This ensures consistent behavior across all scrapers.
     * </p>
     *
     * @param url the URL of the web page to fetch
     * @return the parsed {@link Document} representing the HTML content of the page
     * @throws IOException if an I/O error occurs while fetching the URL
     */
    public Document fetch(String url) throws IOException {
        return Jsoup.connect(url)
            .userAgent(scraperProperties.getUserAgent())
            .timeout(scraperProperties.getTimeout())
            .get();
    }
}
