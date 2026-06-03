package net.engineeringdigest.journalapp.Repository;

import net.engineeringdigest.journalapp.Entity.ConfigAppJournalEntity;
import net.engineeringdigest.journalapp.Entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConfigJournalAppRepository extends MongoRepository<ConfigAppJournalEntity, ObjectId> {

}
