package br.com.oystr.agromachinery.scraping.testutils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Utility class for loading HTML files from the test resources directory.
 * <p>
 * This class helps tests by providing a method to load and parse HTML documents
 * from files located in <code>src/test/resources</code>. It fails the test
 * immediately if the file is not found, avoiding null pointer issues.
 * </p>
 */
public final class TestHtmlFileLoader {

    /**
     * Private constructor to prevent instantiation.
     */
    private TestHtmlFileLoader() {
    }

    /**
     * Loads an HTML file from the classpath into a Jsoup Document.
     *
     * @param resourcePath path to the test HTML file, e.g., "mock_product.html"
     * @return Jsoup Document parsed from the file
     */
    public static Document loadDocument(String resourcePath) {
        resourcePath = "tests/" + resourcePath;
        try (InputStream is = TestHtmlFileLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                fail("Test HTML '%s' file not found!".formatted(resourcePath));
            }

            return Jsoup.parse(is, StandardCharsets.UTF_8.name(), resourcePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load test HTML: " + resourcePath, e);
        }
    }
}
