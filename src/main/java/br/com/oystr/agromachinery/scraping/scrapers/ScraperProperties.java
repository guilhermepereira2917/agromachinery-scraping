package br.com.oystr.agromachinery.scraping.scrapers;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>
 * This class maps scraper-related properties from the application configuration.
 * It allows configuring default values such as the User-Agent header and
 * connection timeout for HTTP requests performed by scrapers.
 * </p>
 */
@Component
@ConfigurationProperties(prefix = "scraper")
public class ScraperProperties {

    /**
     * The User-Agent string to be used in HTTP requests.
     */
    private String userAgent;

    /**
     * The timeout (in milliseconds) for HTTP connections.
     */
    private int timeout;

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
