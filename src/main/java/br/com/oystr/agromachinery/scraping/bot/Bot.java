package br.com.oystr.agromachinery.scraping.bot;

import br.com.oystr.agromachinery.scraping.model.Machine;

/**
 * Represents a web scraper (bot) capable of fetching machine data
 * from a given URL and determining if it supports scraping that URL.
 * <p>
 * Implementations of this interface should handle the parsing of
 * specific websites and return a {@link Machine} object with the
 * extracted data.
 */
public interface Bot {

    /**
     * Fetches machine information from the given URL.
     *
     * @param url the URL of the web page containing the machine details
     * @return a {@link Machine} object populated with data from the page,
     * or {@code null} if the page could not be parsed or an error occurs
     */
    Machine fetch(String url);

    /**
     * Checks whether this bot implementation can scrape data from
     * the given URL.
     *
     * @param url the URL to check
     * @return {@code true} if this bot can handle the URL, {@code false} otherwise
     */
    boolean supports(String url);
}
