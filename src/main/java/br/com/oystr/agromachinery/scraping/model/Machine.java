package br.com.oystr.agromachinery.scraping.model;

import java.math.BigDecimal;

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
    String url
) {
    @Override
    public String toString() {
        return "Machine{" +
            "model='" + model + '\'' +
            ", contractType=" + contractType +
            ", make='" + make + '\'' +
            ", year=" + year +
            ", workedHours=" + workedHours +
            ", city='" + city + '\'' +
            ", price=" + price +
            ", photo='" + photo + '\'' +
            ", url='" + url + '\'' +
            '}';
    }
}
