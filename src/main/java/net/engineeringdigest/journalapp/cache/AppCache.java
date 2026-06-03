package net.engineeringdigest.journalapp.cache;

import jakarta.annotation.PostConstruct;
import net.engineeringdigest.journalapp.Entity.ConfigAppJournalEntity;
import net.engineeringdigest.journalapp.Repository.ConfigJournalAppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppCache {

    public enum Keys{
        WEATHER_API;
    }

    @Autowired
    private ConfigJournalAppRepository configJournalAppRepository;

    public Map<String, String> appCache;

    @PostConstruct
    public void init() {
        appCache = new HashMap<>();
        List<ConfigAppJournalEntity> all = configJournalAppRepository.findAll();
        for(ConfigAppJournalEntity configAppJournalEntity : all) {
            appCache.put(configAppJournalEntity.getKey(), configAppJournalEntity.getContent());
        }

    }


}
