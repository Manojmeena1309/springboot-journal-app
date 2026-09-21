package net.engineeringdigest.journalapp.Controller;

import net.engineeringdigest.journalapp.Entity.ImageAsset;
import net.engineeringdigest.journalapp.Services.ImageService;
import net.engineeringdigest.journalapp.storage.StoredImage;
import net.engineeringdigest.journalapp.storage.ImageStorageException;
import org.bson.types.ObjectId;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/journal/images")
public class ImageController {
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, Authentication authentication) {
        try {
            ImageAsset asset = imageService.upload(file, authentication.getName());
            return ResponseEntity.status(201).body(Map.of(
                    "id", asset.getId().toHexString(),
                    "contentType", asset.getContentType(),
                    "sizeBytes", asset.getSizeBytes()
            ));
        } catch (ImageStorageException exception) {
            if (exception.timedOut()) {
                return ResponseEntity.status(504).body(Map.of("message", "Image upload timed out. Please try again."));
            }
            return ResponseEntity.status(502).body(Map.of("message", "Image storage is temporarily unavailable. Please try again."));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<InputStreamResource> read(@PathVariable ObjectId id, Authentication authentication) {
        StoredImage image = imageService.download(id, authentication.getName());
        MediaType contentType = MediaType.parseMediaType(image.contentType() == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : image.contentType());
        return ResponseEntity.ok()
                .contentType(contentType)
                .contentLength(image.contentLength())
                .header(HttpHeaders.CACHE_CONTROL, "private, max-age=300")
                .body(new InputStreamResource(image.inputStream()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable ObjectId id, Authentication authentication) {
        imageService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
