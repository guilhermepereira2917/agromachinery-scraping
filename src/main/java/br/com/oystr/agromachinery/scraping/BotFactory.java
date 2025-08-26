package br.com.oystr.agromachinery.scraping;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Factory for retrieving the appropriate {@link Bot} implementation
 * based on a given URL.
 * <p>
 * This class maintains a list of available bots and provides a method
 * to find the first bot that supports a particular URL.
 * </p>
 */
@Component
public class BotFactory {

    private final List<Bot> bots;

    public BotFactory(List<Bot> bots) {
        this.bots = bots;
    }

    /**
     * Returns the bot that supports the given URL.
     *
     * @param url the URL to find a bot for
     * @return an {@link Optional} containing the bot if found, or empty if no bot supports the URL
     */
    public Optional<Bot> getRobot(String url) {
        return bots.stream()
            .filter(r -> r.supports(url))
            .findFirst();
    }
}
