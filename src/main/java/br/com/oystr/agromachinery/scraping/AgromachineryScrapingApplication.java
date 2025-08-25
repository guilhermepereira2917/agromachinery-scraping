package br.com.oystr.agromachinery.scraping;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AgromachineryScrapingApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgromachineryScrapingApplication.class, args);
    }

    @Bean
    CommandLineRunner run() {
        return args -> {
            /*
             * TODO: initialize a bot impl class and decide how it's going to be applied/used here.
             */
            Bot b = null;

            /*
             * TODO: If any of these pages does not work, you may look to new ones in the root page for each domain.
             */
            String[] urls = new String[]{
                "https://www.agrofy.com.br/trator-john-deere-7230j-oferta.html",
                "https://www.agrofy.com.br/trator-case-puma-215-193793.html",
                "https://www.tratoresecolheitadeiras.com.br/veiculo/uberlandia/mg/plataforma-colheitadeira/gts/flexer-xs-45/2023/45-pes/draper/triamaq-tratores/1028839",
                "https://www.tratoresecolheitadeiras.com.br/veiculo/uberlandia/mg/plataforma-colheitadeira/gts/produttiva-1250/2022/caracol/12-linhas/triamaq-tratores/994257",
                "https://www.mercadomaquinas.com.br/anuncio/236624-retro-escavadeira-caterpillar-416e-2015-carlopolis-pr",
                "https://www.mercadomaquinas.com.br/anuncio/236623-mini-escavadeira-bobcat-e27z-2019-sete-lagoas-mg"
            };

            for (String s : urls) {

                Machine m = b.fetch(s);

                /*
                 * TODO: print/output data from mA, mB and mC here
                 */
            }
        };
    }
}
