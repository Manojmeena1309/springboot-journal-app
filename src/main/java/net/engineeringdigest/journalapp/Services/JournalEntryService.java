//package net.engineeringdigest.journalapp.Services;
//
//import net.engineeringdigest.journalapp.Entity.JournalEntry;
//import net.engineeringdigest.journalapp.Repository.JournalEntryRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class JournalEntryService {
//
//    @Autowired                     // this is variable name below
//    private JournalEntryRepository journalEntryRepository;
//
//    public void saveEntry(JournalEntry journalEntry){
//       journalEntryRepository.save(journalEntry);
//    }
//
//}

// controller -----> service ---> repository

package net.engineeringdigest.journalapp.Services;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalapp.Entity.JournalEntry;
import net.engineeringdigest.journalapp.Entity.User;
import net.engineeringdigest.journalapp.Repository.JournalEntryRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;

    // saveEntry
    @Transactional
    public void saveEntry(JournalEntry journalEntry, String userName){

        log.info("Creating journal entry for user: {}", userName);

      try{
          User user = userService.findByUserName(userName);

          if(user.getJournalEntries() == null){
              user.setJournalEntries(new ArrayList<>());
          }

          journalEntry.setDate(LocalDateTime.now());
          JournalEntry savedEntry = journalEntryRepository.save(journalEntry);

          user.getJournalEntries().add(savedEntry);
          userService.saveUser(user);

          log.info("Journal entry created successfully for user: {}", userName);

      } catch (Exception e) {

          log.error("Error while creating journal for user: {}", userName, e);

          throw new RuntimeException("An error occurred while saving the entry", e);
      }
    }

    //Update entry
    public void updateEntry(JournalEntry entry){
        journalEntryRepository.save(entry);
    }

    //getAll
    public List<JournalEntry> getAll(){

        return journalEntryRepository.findAll();
    }

    //findById
    public Optional<JournalEntry> findById(ObjectId id){
        return journalEntryRepository.findById(id);
    }

    //deleteById
    @Transactional
    public boolean deleteById(ObjectId id, String userName){

        log.info("Deleting journal {} for user:{}", id, userName);

            User user = userService.findByUserName(userName);

            if(user == null){

                log.warn("User not found during deletion: {}", userName);

                throw new RuntimeException("User not found");
            }

            boolean removed = user.getJournalEntries().removeIf(x -> x.getId().equals(id));

            if(removed){
                userService.saveUser(user);
                journalEntryRepository.deleteById((id));

                log.info("Journal entry deleted successfully for user: {}", userName);
            }
            else{
                log.warn("User not found during deletion: {}", userName);
            }

        return removed;
    }
}

// user relies on List<JournalEntry>, so: if u don't update user-> data becomes inconsistent,
// that's why both operations must happen together. that's why @Transactional