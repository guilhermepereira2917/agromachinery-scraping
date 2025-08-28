package br.com.oystr.agromachinery.scraping.model;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Represents a machine listing fetched from a web page.
 * <p>
 * This record contains details such as the model, make, year,
 * hours of use, location, price, image URL, and the page URL
 * where the machine is listed.
 * </p>
 */
public record Machine(
    String model,
    ContractType contractType,
    String make,
    Integer year,
    Integer workedHours,
    String city,
    BigDecimal price,
    String photo,
    String photoBase64,
    String url
) {
    @Override
    public String toString() {
        String formattedPrice = price != null
            ? NumberFormat.getCurrencyInstance(Locale.of("pt", "BR")).format(price)
            : "N/A";

        return "Machine {" + System.lineSeparator() +
            "  model='" + model + '\'' + "," + System.lineSeparator() +
            "  contractType=" + contractType + "," + System.lineSeparator() +
            "  make='" + make + '\'' + "," + System.lineSeparator() +
            "  year=" + year + "," + System.lineSeparator() +
            "  workedHours=" + workedHours + "," + System.lineSeparator() +
            "  city='" + city + '\'' + "," + System.lineSeparator() +
            "  price=" + formattedPrice + "," + System.lineSeparator() +
            "  photo='" + photo + '\'' + "," + System.lineSeparator() +
            "  photoBase64='data:image/png;base64," +
            (photoBase64 != null ? photoBase64.substring(0, 30) + "..." : null) + "'," + System.lineSeparator() +
            "  url='" + url + '\'' + System.lineSeparator() +
            "}";
    }
}
