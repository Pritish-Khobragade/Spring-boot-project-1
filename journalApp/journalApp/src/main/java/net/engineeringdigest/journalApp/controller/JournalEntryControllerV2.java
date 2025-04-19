package net.engineeringdigest.journalApp.controller;

import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.service.JournalEntryService;
import net.engineeringdigest.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.KeyStore;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/journal")
public class JournalEntryControllerV2 {

    @Autowired
    private UserService userService;

    @Autowired
    private JournalEntryService journalEntryService;

    @GetMapping("/journalEntries/{userName}")
    public ResponseEntity<List<JournalEntry>> getAllJournalEntriesOfUser(@PathVariable String userName) {
        User user = userService.findByUserName(userName);
        List<JournalEntry> all = journalEntryService.getAll();
        {
            if (all != null && !all.isEmpty()) {
                return new ResponseEntity<>(all, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @PostMapping
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry myEntry, @PathVariable String userName) {
        try {
            User user = userService.findByUserName(userName);
            journalEntryService.saveEntry(myEntry);
            return new ResponseEntity<>(myEntry, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("id/{myId}")
    public JournalEntry getJournalEntryById(@PathVariable String myId) {
        return journalEntryService.findById(new ObjectId(myId)).orElse(null);
    }

    @DeleteMapping("id/{myId}")
    public boolean deleteJournalEntryById(@PathVariable String myId) {
        journalEntryService.deleteById(new ObjectId(myId));
        return true;
    }

    @PutMapping("id/{id}")
    public JournalEntry updateJournalEntryById(@PathVariable String id, @RequestBody JournalEntry myEntry) {
        Optional<JournalEntry> optionalOldEntry = journalEntryService.findById(new ObjectId(id));

        if (optionalOldEntry.isPresent()) {
            JournalEntry old = optionalOldEntry.get();

            // Update fields only if new values are provided
            if (myEntry.getTitle() != null && !myEntry.getTitle().isEmpty()) {
                old.setTitle(myEntry.getTitle());
            }
            if (myEntry.getContent() != null && !myEntry.getContent().isEmpty()) {
                old.setContent(myEntry.getContent());
            }

            journalEntryService.saveEntry(old);
            return old;
        }

        return null; // Or throw an exception if preferred
    }
}
