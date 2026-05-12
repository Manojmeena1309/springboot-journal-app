package net.engineeringdigest.journalapp.Controller;


import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalapp.Entity.JournalEntry;
import net.engineeringdigest.journalapp.Entity.User;
import net.engineeringdigest.journalapp.Services.JournalEntryService;
import net.engineeringdigest.journalapp.Services.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/journal")
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    // Get all journals of logged-in user
    @GetMapping
    public ResponseEntity<?> getAllJournalEntriesOfUser(Authentication authentication){

        String userName = authentication.getName();

        log.info("Fetching all journal entries for user : {} ", userName);

        User user = userService.findByUserName(userName);

        if(user == null){

            log.warn("No journal entries found for user: {}", userName);

            return ResponseEntity.notFound().build();
        }

        List<JournalEntry> entries = user.getJournalEntries();

        if(entries == null || entries.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(entries);
    }

    // Create entry
    @PostMapping
    public ResponseEntity<?> createEntry(@RequestBody JournalEntry myEntry, Authentication authentication){

        String userName = authentication.getName();

        log.info("Received journal creation request from user: {}", userName);

        journalEntryService.saveEntry(myEntry, userName);

        return ResponseEntity.status(HttpStatus.CREATED).body(myEntry);
    }


    // Get entry by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getJournalEntryById(@PathVariable ObjectId id, Authentication authentication){

        String userName = authentication.getName();
        User user = userService.findByUserName(userName);

        if(user == null){
            return  ResponseEntity.notFound().build();
        }

        List<JournalEntry> entries = user.getJournalEntries();

        if(entries == null){
            return  ResponseEntity.notFound().build();
        }

        boolean exists = entries.stream().anyMatch(x -> x.getId().equals(id));

        if(!exists){
            log.warn("Unauthorized journal access attempt by user {} for journal {}",
                    userName, id);
          return  ResponseEntity.notFound().build();
        }

        return journalEntryService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Delete entry
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJournalEntryById(@PathVariable ObjectId id, Authentication authentication){

        String userName = authentication.getName();

        log.info("Received delete request for journal {} by user {}",
                id, userName);

        boolean removed = journalEntryService.deleteById(id, userName);

        return  removed ? ResponseEntity.ok("Deleted") : ResponseEntity.notFound().build();
    }

    // Update entry
    @PutMapping("/{id}")
    public ResponseEntity<?> updateJournalById(@PathVariable ObjectId id, @RequestBody JournalEntry newEntry, Authentication authentication){

        String userName = authentication.getName();

        log.info("Received update request for journal {} by user {}",
                id, userName);

        User user = userService.findByUserName(userName);

        if(user == null){
            return ResponseEntity.notFound().build();
        }

        List<JournalEntry> entries = user.getJournalEntries();

        if(entries == null){
            return ResponseEntity.notFound().build();
        }

        boolean exists = entries.stream().anyMatch(x -> x.getId().equals(id));

        if(!exists){

            log.warn("Unauthorized update attempt on journal {} by user {}",
                    id, userName);
            return ResponseEntity.notFound().build();
        }

        Optional<JournalEntry> journalEntry = journalEntryService.findById(id);

        if(journalEntry.isEmpty()){
            return  ResponseEntity.notFound().build();
        }

        JournalEntry old = journalEntry.get();

        if(newEntry.getTitle() != null && !newEntry.getTitle().isEmpty()){
            old.setTitle((newEntry.getTitle()));
        }

        if(newEntry.getContent() != null && !newEntry.getContent().isEmpty()){
            old.setContent(newEntry.getContent());
        }

        journalEntryService.updateEntry(old);

        return ResponseEntity.ok(old);
    }

}
