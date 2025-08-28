package br.com.oystr.agromachinery.scraping;

import br.com.oystr.agromachinery.scraping.bot.BotFactory;
import br.com.oystr.agromachinery.scraping.model.Machine;
import br.com.oystr.agromachinery.scraping.service.ScraperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * Main Spring Boot application class for the Agromachinery scraping tool.
 *
 * <p>
 * This application orchestrates the scraping of machinery data from multiple
 * agricultural websites using scrapers provided by {@link BotFactory}. The
 * URLs to be scraped are configured in a {@link CommandLineRunner} bean.
 * </p>
 *
 * <p>
 * The {@link CommandLineRunner} executes scraping tasks concurrently using a
 * thread pool executor. Each task selects the appropriate scraper for the URL,
 * fetches the machine data, and logs the resulting {@link Machine} instances.
 * </p>
 */
@SpringBootApplication
public class AgromachineryScrapingApplication {

    private final ScraperService scraperService;

    @Value("${scraper.urls}")
    private List<String> urls;

    private static final Logger log = LoggerFactory.getLogger(AgromachineryScrapingApplication.class);

    public AgromachineryScrapingApplication(ScraperService scraperService) {
        this.scraperService = scraperService;
    }

    public static void main(String[] args) {
        SpringApplication.run(AgromachineryScrapingApplication.class, args);
    }

    @Bean
    CommandLineRunner run() {
        return args -> {
            List<Machine> machines = scraperService.scrape(urls);
            log.info("Successfully fetched {} machines.", machines.size());
        };
    }
}
