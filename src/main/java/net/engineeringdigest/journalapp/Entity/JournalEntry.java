package net.engineeringdigest.journalapp.Entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Date;

@Document(collection = "journal_entries")
@Data  // equivalent to getter, setter
@NoArgsConstructor

public class JournalEntry {

    @Id
    @JsonProperty("id")
    private ObjectId id;

    @NonNull
    private String title;

    private String content;

    private LocalDateTime date = LocalDateTime.now();

    @JsonProperty("id")
    public String getIdAsString() {
        return id != null ? id.toHexString() : null;
    }
}
