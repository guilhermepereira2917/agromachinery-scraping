package br.com.oystr.agromachinery.scraping.exceptions;

/**
 * Exception thrown when a machine listing cannot be found or parsed
 * from a given web page.
 */
public class MachineNotFoundException extends RuntimeException {
    public MachineNotFoundException(String message) {
        super(message);
    }

    public MachineNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
