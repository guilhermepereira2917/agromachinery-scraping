package br.com.oystr.agromachinery.scraping.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Optional;

public class PriceParser {

    private PriceParser() {
    }

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
