package net.engineeringdigest.journalapp.Controller;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalapp.Entity.User;
import net.engineeringdigest.journalapp.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
public class  AdminController {

    @Autowired
    private UserService userService;

    // Get all users
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(){

        List<User> users = userService.getAll();

        if(users.isEmpty()){
           return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("No users found");
        }

        return ResponseEntity.ok(users);
    }

    // create admin user
    @PostMapping("/user")
    public ResponseEntity<?> createAdmin(@RequestBody User user){

       try{
           log.warn("Admin creation request for user: {}",
                   user.getUserName());
           userService.saveAdmin(user);
           return ResponseEntity.status(HttpStatus.CREATED).body("Admin user created");
       } catch (Exception e) {

           log.error("Admin creation failed for user: {}",
                   user.getUserName(), e);
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating admin user");
       }
    }
}
