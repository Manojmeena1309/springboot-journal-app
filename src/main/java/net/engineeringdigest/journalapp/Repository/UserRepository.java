package net.engineeringdigest.journalapp.Repository;

import net.engineeringdigest.journalapp.Entity.JournalEntry;
import net.engineeringdigest.journalapp.Entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, ObjectId> {

    User findByUserName(String username);  //select * from users where userName = ?

    void deleteByUserName(String username); //deletes user by username
}
