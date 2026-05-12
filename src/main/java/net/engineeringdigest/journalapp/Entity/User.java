package net.engineeringdigest.journalapp.Entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexOptions;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@Data  // equivalent to getter, setter
@Builder

public class User {

    @Id
    private ObjectId id;
    @Indexed(unique = true)  // ensures username is unique in DB
    @NonNull
    private String userName;
    @NonNull
    private String password;  // this will store encoded password

    @DBRef   // user -> journal relationship
    private List<JournalEntry> journalEntries = new ArrayList<>();
    private List<String> roles;  // for spring security "user" "admin"

}
