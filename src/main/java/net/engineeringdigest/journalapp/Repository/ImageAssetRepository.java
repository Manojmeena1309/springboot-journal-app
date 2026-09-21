package net.engineeringdigest.journalapp.Repository;

import net.engineeringdigest.journalapp.Entity.ImageAsset;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImageAssetRepository extends MongoRepository<ImageAsset, ObjectId> {
}
