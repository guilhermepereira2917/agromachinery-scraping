package br.com.oystr.agromachinery.scraping.model;

/**
 * Represents the type of contract for a machine listing.
 * <p>
 * <ul>
 *     <li>{@link #RENT} – the machine is available for rental.</li>
 *     <li>{@link #SALE} – the machine is available for sale.</li>
 * </ul>
 * </p>
 */
public enum ContractType {
    RENT, SALE;
}
