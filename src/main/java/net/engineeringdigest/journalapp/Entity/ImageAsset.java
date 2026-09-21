package net.engineeringdigest.journalapp.Entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "image_assets")
@Data
@NoArgsConstructor
public class ImageAsset {
    @Id
    private ObjectId id;

    @Indexed
    private String ownerUserName;
    private String storageKey;
    private String originalFilename;
    private String contentType;
    private long sizeBytes;
    private LocalDateTime createdAt = LocalDateTime.now();
}
