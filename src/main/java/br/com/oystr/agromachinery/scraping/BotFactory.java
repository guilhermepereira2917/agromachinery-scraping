package br.com.oystr.agromachinery.scraping;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BotFactory {

    private final List<Bot> bots;

    public BotFactory(List<Bot> bots) {
        this.bots = bots;
    }

    public Optional<Bot> getRobot(String url) {
        return bots.stream()
            .filter(r -> r.supports(url))
            .findFirst();
    }
}
