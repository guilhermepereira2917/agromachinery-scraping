package br.com.oystr.agromachinery.scraping.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URI;
import java.util.Base64;
import java.util.Optional;

/**
 * Utility class for converting remote images (fetched by URL) into Base64-encoded strings.
 * <p>
 * This class provides a static method to retrieve an image from a given URL,
 * read its bytes, and return the result as a Base64-encoded {@link String}.
 * <br>
 */
public class ImageConverter {

    private static final Logger log = LoggerFactory.getLogger(ImageConverter.class);

    /**
     * Private constructor to prevent instantiation.
     */
    private ImageConverter() {
    }

    /**
     * Converts the image found at the given URL into a Base64-encoded string.
     *
     * @param imageUrl the URL of the image to fetch and convert
     * @return an {@link Optional} containing the Base64-encoded string if successful,
     * or an empty {@link Optional} if the image could not be fetched or converted
     */
    public static Optional<String> convertImageToBase64(String imageUrl) {
        try (InputStream in = URI.create(imageUrl).toURL().openStream()) {
            byte[] bytes = in.readAllBytes();

            return Optional.of(Base64.getEncoder().encodeToString(bytes));
        } catch (Exception e) {
            log.warn("Failed to fetch or convert image from URL: {}", imageUrl, e);

            return Optional.empty();
        }
    }
}
