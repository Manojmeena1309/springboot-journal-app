package net.engineeringdigest.journalapp.Controller;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalapp.Entity.User;
import net.engineeringdigest.journalapp.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserService userService;

    @GetMapping("/health-check")  // Is the Application Alive and working?
    public String healthCheck(){
        return "OK";
    }

    @PostMapping("/user")
    public ResponseEntity<?> createUser(@RequestBody User user){

        try{

            log.info("Public registration request for user: {}",
                    user.getUserName());

            userService.saveNewUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
        }catch (Exception e){

            log.error("Public registration failed for user: {}",
                    user.getUserName(), e);

            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists or invalid data");
        }

    }
}
