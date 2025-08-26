package br.com.oystr.agromachinery.scraping.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Optional;

/**
 * Utility class for parsing price strings into {@link BigDecimal} values.
 * <p>
 * This class provides a static method to convert Brazilian-formatted
 * currency strings (e.g., "R$ 1.234,56") into {@link BigDecimal}.
 * </p>
 *
 * <p>The constructor is private to prevent instantiation.</p>
 */
public class PriceParser {

    /**
     * Private constructor to prevent instantiation.
     */
    private PriceParser() {
    }

    /**
     * Parses a price string formatted in Brazilian currency into a {@link BigDecimal}.
     *
     * @param priceString the price string to parse, e.g., "R$ 1.234,56"
     * @return an {@link Optional} containing the parsed {@link BigDecimal},
     * or {@link Optional#empty()} if the input is null, blank, or unparseable
     */
    public static Optional<BigDecimal> parsePrice(String priceString) {
        if (priceString == null || priceString.isBlank()) {
            return Optional.empty();
        }

        NumberFormat format = NumberFormat.getInstance(Locale.of("pt", "BR"));
        String cleanedPriceString = priceString.replace("R$ ", "").replace(" ", "");

        try {
            Number number = format.parse(cleanedPriceString);
            return Optional.of(BigDecimal.valueOf(number.doubleValue()));
        } catch (ParseException e) {
            return Optional.empty();
        }
    }
}
