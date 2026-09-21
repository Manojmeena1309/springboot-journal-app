package net.engineeringdigest.journalapp.storage;

import java.io.InputStream;

public record StoredImage(InputStream inputStream, String contentType, long contentLength) {
}
