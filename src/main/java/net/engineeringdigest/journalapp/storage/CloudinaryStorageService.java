package net.engineeringdigest.journalapp.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.URL;

/** Cloudinary-backed implementation of the provider-neutral image storage contract. */
@Service
@Slf4j
public class CloudinaryStorageService implements ImageStorageService {
    private final String cloudinaryUrl;
    private final int timeoutMs;

    public CloudinaryStorageService(
            @Value("${cloudinary.url:}") String cloudinaryUrl,
            @Value("${cloudinary.timeout-ms:30000}") int timeoutMs) {
        this.cloudinaryUrl = cloudinaryUrl;
        this.timeoutMs = timeoutMs;
    }

    private Cloudinary client() {
        if (cloudinaryUrl.isBlank()) {
            throw new IllegalStateException("Cloudinary is not configured on this server.");
        }
        return new Cloudinary(cloudinaryUrl);
    }

    @Override
    public void upload(String storageKey, InputStream inputStream, long contentLength, String contentType) {
        log.info("Cloudinary image upload started.");
        try {
            byte[] imageBytes = inputStream.readAllBytes();
            client().uploader().upload(imageBytes, ObjectUtils.asMap(
                    "public_id", storageKey,
                    "resource_type", "image",
                    "overwrite", false,
                    "unique_filename", false,
                    "use_filename", false,
                    "format", formatFor(contentType),
                    "timeout", timeoutMs,
                    "connect_timeout", timeoutMs,
                    "connection_request_timeout", timeoutMs
            ));
            log.info("Cloudinary image upload succeeded.");
        } catch (InterruptedIOException exception) {
            log.warn("Cloudinary image upload timed out: {}", safeMessage(exception.getMessage()));
            throw ImageStorageException.timedOut(exception);
        } catch (IOException exception) {
            log.warn("Cloudinary image upload failed: {}: {}", exception.getClass().getSimpleName(), safeMessage(exception.getMessage()));
            throw ImageStorageException.failed(exception);
        } catch (RuntimeException exception) {
            log.warn("Cloudinary image upload failed: {}: {}", exception.getClass().getSimpleName(), safeMessage(exception.getMessage()));
            throw ImageStorageException.failed(exception);
        }
    }

    @Override
    public StoredImage download(String storageKey) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(
                    client().url().secure(true).resourceType("image").generate(storageKey)
            ).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            if (connection.getResponseCode() >= 400) {
                connection.disconnect();
                throw new IllegalStateException("The stored image could not be retrieved.");
            }
            InputStream stream = new FilterInputStream(connection.getInputStream()) {
                @Override
                public void close() throws IOException {
                    try {
                        super.close();
                    } finally {
                        connection.disconnect();
                    }
                }
            };
            return new StoredImage(stream, connection.getContentType(), connection.getContentLengthLong());
        } catch (IOException exception) {
            throw new IllegalStateException("The stored image could not be retrieved.", exception);
        }
    }

    @Override
    public void delete(String storageKey) {
        try {
            client().uploader().destroy(storageKey, ObjectUtils.asMap("resource_type", "image", "invalidate", true));
        } catch (IOException exception) {
            throw new IllegalStateException("Cloudinary could not delete the image.", exception);
        }
    }

    private String formatFor(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> throw new IllegalArgumentException("Unsupported image type.");
        };
    }

    private String safeMessage(String message) {
        if (message == null || message.isBlank()) return "<none>";
        return message
                .replaceAll("(?i)cloudinary://\\S+", "cloudinary://<redacted>")
                .replaceAll("https?://\\S+", "<url-redacted>")
                .replaceAll("(?i)(api[_ -]?secret|api[_ -]?key|password|token|signature|authorization)\\s*[=:]\\s*[^\\s,;&]+", "$1=<redacted>")
                .replaceAll("(?i)(api[_ -]?secret|api[_ -]?key|password|token|signature|authorization)\\s+[^\\s,;&]+", "$1 <redacted>");
    }
}
