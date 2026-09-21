package net.engineeringdigest.journalapp.Entity;

import lombok.*;
import net.engineeringdigest.journalapp.enums.Sentiment;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

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

    /**
     * Optional Tiptap document JSON. The legacy content field remains as a
     * plain-text fallback and card preview for entries created before rich text.
     */
    private Map<String, Object> richContent;

    private LocalDateTime date = LocalDateTime.now();

    private Sentiment sentiment;

    @JsonProperty("id")
    public String getIdAsString() {
        return id != null ? id.toHexString() : null;
    }
}
