package net.engineeringdigest.journalapp.Services;

import net.engineeringdigest.journalapp.Entity.ImageAsset;
import net.engineeringdigest.journalapp.Repository.ImageAssetRepository;
import net.engineeringdigest.journalapp.storage.ImageStorageService;
import net.engineeringdigest.journalapp.storage.ImageStorageException;
import net.engineeringdigest.journalapp.storage.StoredImage;
import org.bson.types.ObjectId;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageService {
    public static final long MAX_IMAGE_SIZE_BYTES = 10 * 1024 * 1024;
    private static final Set<String> ALLOWED_TYPES = Set.of(MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, "image/webp");

    private final ImageAssetRepository imageAssetRepository;
    private final ImageStorageService imageStorageService;

    public ImageService(ImageAssetRepository imageAssetRepository, ImageStorageService imageStorageService) {
        this.imageAssetRepository = imageAssetRepository;
        this.imageStorageService = imageStorageService;
    }

    public ImageAsset upload(MultipartFile file, String userName) {
        validate(file);
        ImageAsset asset = new ImageAsset();
        asset.setOwnerUserName(userName);
        asset.setOriginalFilename(file.getOriginalFilename());
        asset.setContentType(file.getContentType());
        asset.setSizeBytes(file.getSize());
        asset.setCreatedAt(LocalDateTime.now());
        asset.setStorageKey("journals/" + safePathPart(userName) + "/" + UUID.randomUUID());

        try (InputStream inputStream = file.getInputStream()) {
            imageStorageService.upload(asset.getStorageKey(), inputStream, file.getSize(), file.getContentType());
            return imageAssetRepository.save(asset);
        } catch (IOException exception) {
            throw new IllegalArgumentException("The uploaded image could not be read.", exception);
        } catch (ImageStorageException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new IllegalStateException("The image could not be stored. Please try again.", exception);
        }
    }

    public StoredImage download(ObjectId assetId, String userName) {
        ImageAsset asset = findOwned(assetId, userName);
        return imageStorageService.download(asset.getStorageKey());
    }

    public void delete(ObjectId assetId, String userName) {
        ImageAsset asset = findOwned(assetId, userName);
        imageStorageService.delete(asset.getStorageKey());
        imageAssetRepository.delete(asset);
    }

    public void validateAssetsOwnedBy(Collection<String> assetIds, String userName) {
        for (String id : assetIds) {
            try {
                findOwned(new ObjectId(id), userName);
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException("The journal contains an invalid image reference.");
            }
        }
    }

    private ImageAsset findOwned(ObjectId assetId, String userName) {
        ImageAsset asset = imageAssetRepository.findById(assetId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found."));
        if (!asset.getOwnerUserName().equals(userName)) {
            throw new IllegalArgumentException("Image not found.");
        }
        return asset;
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("Choose an image to upload.");
        if (!ALLOWED_TYPES.contains(file.getContentType())) throw new IllegalArgumentException("Only JPG, PNG, and WEBP images are supported.");
        if (file.getSize() > MAX_IMAGE_SIZE_BYTES) throw new IllegalArgumentException("Images must be 10 MB or smaller.");
    }

    private String safePathPart(String value) {
        return value.replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}
