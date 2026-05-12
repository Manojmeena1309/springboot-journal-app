package net.engineeringdigest.journalapp.Repository;

import net.engineeringdigest.journalapp.Entity.JournalEntry;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JournalEntryRepository extends MongoRepository<JournalEntry, ObjectId> {
}
// means Spring, create all database operations for JournalEntry automatically
// because of MongoRepository u automatically get, save(entry), findById(id), findAll(), deleteById(id)