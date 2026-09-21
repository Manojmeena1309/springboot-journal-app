package net.engineeringdigest.journalapp.storage;

import java.io.InputStream;

/** Provider-neutral contract for persistent image object storage. */
public interface ImageStorageService {
    void upload(String storageKey, InputStream inputStream, long contentLength, String contentType);
    StoredImage download(String storageKey);
    void delete(String storageKey);
}
