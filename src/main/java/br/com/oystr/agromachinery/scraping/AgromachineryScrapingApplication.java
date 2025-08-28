package br.com.oystr.agromachinery.scraping;

import br.com.oystr.agromachinery.scraping.bot.BotFactory;
import br.com.oystr.agromachinery.scraping.model.Machine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Main Spring Boot application class for the Agromachinery scraping tool.
 *
 * <p>
 * This application fetches machine data from multiple agricultural websites
 * using scrapers provided by {@link BotFactory}. The URLs to be scraped are
 * defined in the {@link CommandLineRunner} bean.
 * </p>
 *
 * <p>
 * The {@link CommandLineRunner} submits scraping tasks to a thread pool executor,
 * allowing multiple URLs to be processed concurrently. Each task selects the
 * appropriate scraper, fetches the machine data, and logs the resulting {@link Machine}.
 * </p>
 */
@SpringBootApplication
public class AgromachineryScrapingApplication {

    private final BotFactory botFactory;

    @Value("${scraper.threads-count}")
    private int threadsCount;

    @Value("${scraper.await-termination-seconds}")
    private int awaitTerminationSeconds;

    @Value("${scraper.urls}")
    private List<String> urls;

    private static final Logger log = LoggerFactory.getLogger(AgromachineryScrapingApplication.class);

    public AgromachineryScrapingApplication(BotFactory botFactory) {
        this.botFactory = botFactory;
    }

    public static void main(String[] args) {
        SpringApplication.run(AgromachineryScrapingApplication.class, args);
    }

    @Bean
    CommandLineRunner run() {
        return args -> {
            try (ExecutorService executorService = Executors.newFixedThreadPool(threadsCount)) {
                for (String url : urls) {
                    executorService.submit(() ->
                        botFactory.getRobot(url).ifPresentOrElse(
                            robot -> {
                                Machine machine = robot.fetch(url);
                                if (machine != null) {
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
        };
    }
}
