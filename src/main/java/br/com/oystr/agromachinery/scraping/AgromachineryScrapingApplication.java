package br.com.oystr.agromachinery.scraping;

import br.com.oystr.agromachinery.scraping.bot.BotFactory;
import br.com.oystr.agromachinery.scraping.model.Machine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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
 * The {@link CommandLineRunner} iterates through a set of URLs, selects
 * the appropriate scraper, and logs the fetched {@link Machine} objects.
 * </p>
 */
@SpringBootApplication
public class AgromachineryScrapingApplication {

    private final BotFactory botFactory;

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
            String[] urls = new String[]{
                "https://www.agrofy.com.br/trator-magnum-315.html",
                "https://www.agrofy.com.br/trator-john-deere-8320r-204540.html",
                "https://www.tratoresecolheitadeiras.com.br/veiculo/uberlandia/mg/plataforma-colheitadeira/gts/flexer-xs-45/2023/45-pes/draper/triamaq-tratores/1028839",
                "https://www.tratoresecolheitadeiras.com.br/veiculo/fernandopolis/sp/colheitadeira/john-deere/john-deere-s550/2022/-rotor-axial/cabine-cabinado/agro-novaes-maquinas-agricolas/1279673",
                "https://www.mercadomaquinas.com.br/anuncio/247160-kombi-2013-2014-2013-franco-da-rocha-sp",
                "https://www.mercadomaquinas.com.br/anuncio/236623-mini-escavadeira-bobcat-e27z-2019-sete-lagoas-mg"
            };

            for (String url : urls) {
                botFactory.getRobot(url).ifPresentOrElse(
                    robot -> {
                        Machine machine = robot.fetch(url);
                        log.info("Fetched machine: {}", machine);
                    },
                    () -> log.warn("No scraper found for URL: {}", url)
                );
            }
        };
    }
}
