package net.engineeringdigest.journalapp.Services;

import net.engineeringdigest.journalapp.Entity.JournalEntry;
import net.engineeringdigest.journalapp.Entity.User;
import net.engineeringdigest.journalapp.Repository.JournalEntryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.bson.types.ObjectId;

@ExtendWith(MockitoExtension.class)
public class JournalEntryServiceTest {

    @Mock
    private JournalEntryRepository journalEntryRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private JournalEntryService journalEntryService;

    @Test
    void saveEntryTest(){

        User user = User.builder()
                .userName("Devansh")
                .password("Devansh")
                .journalEntries(new ArrayList<>())
                .build();

        JournalEntry journalEntry = new JournalEntry();
        journalEntry.setTitle("My Title");
        journalEntry.setContent("My Content");

        when(userService.findByUserName("Devansh"))
                .thenReturn(user);

        when(journalEntryRepository.save(any(JournalEntry.class)))
                .thenReturn(journalEntry);

        journalEntryService.saveEntry(journalEntry, "Devansh");

        verify(journalEntryRepository, times(1))
                .save(journalEntry);

        verify(userService, times(1))
                .saveUser(user);
    }


    @Test
    void deleteByIdTest(){

        User user = User.builder()
                .userName("Prince")
                .password("Prince")
                .journalEntries(new ArrayList<>())
                .build();

        JournalEntry entry = new JournalEntry();
        entry.setTitle("Test");
        entry.setId(new ObjectId());

        user.getJournalEntries().add(entry);

        when(userService.findByUserName("Prince"))
                .thenReturn(user);

        boolean removed = journalEntryService.deleteById(entry.getId(), "Prince");

        verify(journalEntryRepository, times(1))
                .deleteById(entry.getId());

        verify(userService, times(1))
                .saveUser(user);

        assert(removed);
    }
}