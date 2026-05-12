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
import net.engineeringdigest.journalapp.Entity.User;
import net.engineeringdigest.journalapp.Repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

//    private static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private PasswordEncoder passwordEncoder;

    //when creating new user (POST/USER)
    public void saveNewUser(User user){
        log.info("Saving new user: {}", user.getUserName());

        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(List.of("USER"));
            userRepository.save(user);

            log.info("User {} saved", user.getUserName());
        }catch (Exception e){

            log.error("Error while saving user {}", user.getUserName(), e);

            throw e;
        }

    }

    public void saveAdmin(User user){

        log.warn("Creating admin user: {}", user.getUserName());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(List.of("USER", "ADMIN"));
        userRepository.save(user);

        log.warn("Admin user{} created Successfully", user.getUserName());
    }

    // when updating internally (like adding journal)
    public void saveUser(User user){
        userRepository.save(user);
    }

    public List<User> getAll(){
        return userRepository.findAll();
    }

    public Optional<User> findById(ObjectId id){
        return userRepository.findById(id);
    }

    public void deleteById(ObjectId id){

        log.warn("Deleting user with id : {}", id);

        userRepository.deleteById((id));

        log.info("User deleted Successfully: {}", id);
    }

    public User findByUserName (String userName){
        return userRepository.findByUserName((userName));
    }
}