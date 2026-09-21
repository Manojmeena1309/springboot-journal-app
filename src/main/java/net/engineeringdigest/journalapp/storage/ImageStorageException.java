package net.engineeringdigest.journalapp.storage;

/** Indicates that the configured image storage provider could not complete an operation. */
public class ImageStorageException extends RuntimeException {
    private final boolean timedOut;

    private ImageStorageException(String message, boolean timedOut, Throwable cause) {
        super(message, cause);
        this.timedOut = timedOut;
    }

    public static ImageStorageException timedOut(Throwable cause) {
        return new ImageStorageException("Image storage request timed out.", true, cause);
    }

    public static ImageStorageException failed(Throwable cause) {
        return new ImageStorageException("Image storage request failed.", false, cause);
    }

    public boolean timedOut() {
        return timedOut;
    }
}
