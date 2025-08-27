package br.com.oystr.agromachinery.scraping.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${scraper.user-agent}")
    private String userAgent;

    @Value("${scraper.timeout}")
    private int timeout;

    /**
     * Fetches and parses an HTML document from the given URL using {@link Jsoup}.
     * <p>
     * The request uses the configured User-Agent and timeout from application properties,
     * ensuring consistent and configurable behavior across all scrapers.
     * </p>
     *
     * @param url the URL of the web page to fetch
     * @return the parsed {@link Document} representing the HTML content of the page
     * @throws IOException if an I/O error occurs while fetching the URL
     */
    public Document fetch(String url) throws IOException {
        return Jsoup.connect(url)
            .userAgent(userAgent)
            .timeout(timeout)
            .get();
    }
}
