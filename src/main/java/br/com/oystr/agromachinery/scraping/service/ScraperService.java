package br.com.oystr.agromachinery.scraping.service;

import br.com.oystr.agromachinery.scraping.AgromachineryScrapingApplication;
import br.com.oystr.agromachinery.scraping.bot.BotFactory;
import br.com.oystr.agromachinery.scraping.model.Machine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Service responsible for orchestrating the scraping of machinery listings
 * from various URLs using configured scraper bots.
 * <p>
 * Each URL is processed concurrently using a thread pool. The service ensures
 * proper shutdown of the executor and logs the results of each scraping task.
 * </p>
 */
@Service
public class ScraperService {

    private final BotFactory botFactory;

    @Value("${scraper.threads-count}")
    private int threadsCount;

    @Value("${scraper.await-termination-seconds}")
    private int awaitTerminationSeconds;

    private static final Logger log = LoggerFactory.getLogger(AgromachineryScrapingApplication.class);

    public ScraperService(BotFactory botFactory) {
        this.botFactory = botFactory;
    }

    /**
     * Starts scraping machinery data from the provided list of URLs.
     *
     * <p>
     * Each URL is processed in a separate thread from a fixed-size thread pool.
     * The service will wait for a maximum of {@code awaitTerminationSeconds} for
     * all tasks to complete before forcing shutdown. Individual scraping results
     * are logged; if a scraper is not found for a URL, a warning is logged.
     * </p>
     *
     * @param urls List of URLs to scrape machinery data from
     * @return A list of {@link Machine} objects representing the successfully
     *         fetched machines. URLs that could not be scraped (e.g., scraper
     *         not found or fetch error) are not included in the list.
     */
    public List<Machine> scrape(List<String> urls) {
        List<Machine> machines = Collections.synchronizedList(new ArrayList<>());

        try (ExecutorService executorService = Executors.newFixedThreadPool(threadsCount)) {
            for (String url : urls) {
                executorService.submit(() ->
                    botFactory.getRobot(url).ifPresentOrElse(
                        robot -> {
                            Machine machine = robot.fetch(url);
                            if (machine != null) {
                                machines.add(machine);
                                log.info("Fetched machine: {}", machine);
                            }
                        },
                        () -> log.warn("No scraper found for URL: {}", url)
                    )
                );
            }

            executorService.shutdown();
            log.info("Executor service shutdown initiated. Waiting up to {} seconds for tasks to complete...", awaitTerminationSeconds);

            try {
                if (!executorService.awaitTermination(awaitTerminationSeconds, TimeUnit.SECONDS)) {
                    log.warn("Executor service did not terminate within {} seconds. Forcing shutdown now.", awaitTerminationSeconds);
                    executorService.shutdownNow();
                } else {
                    log.info("Executor service terminated gracefully");
                }
            } catch (InterruptedException e) {
                log.error("Shutdown interrupted. Forcing executor service shutdown now.", e);
                executorService.shutdownNow();
            }
        }

        return new ArrayList<>(machines);
    }
}
