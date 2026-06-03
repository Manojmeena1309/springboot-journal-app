package net.engineeringdigest.journalapp.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "config_journal_app")
@Data  // equivalent to getter, setter
@NoArgsConstructor

public class ConfigAppJournalEntity {

    private String key;
    private String content;

}
