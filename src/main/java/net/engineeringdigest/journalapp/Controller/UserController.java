package net.engineeringdigest.journalapp.Controller;


import net.engineeringdigest.journalapp.Entity.User;
import net.engineeringdigest.journalapp.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

// this class will contain all special type of components

@RestController
@RequestMapping("/user")  // this adds mapping on whole class
public class UserController {

    @Autowired
    private UserService userService;

    // update logged-in user
    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User user){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User userInDb = userService.findByUserName(userName);

        if(userInDb == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        userInDb.setUserName(user.getUserName());

        // encode Password again
        userInDb.setPassword(user.getPassword());

        userService.saveNewUser(userInDb);

        return ResponseEntity.ok("User updated");
    }

    // Delete logged-in user
    @DeleteMapping
    public ResponseEntity<?> deleteUser(Authentication authentication){

        String userName = authentication.getName();

        User user = userService.findByUserName(userName);

        if(user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        userService.deleteById(user.getId());

        return  ResponseEntity.ok("User deleted");
    }
}

// not using deleteByUserName method from userRepository cause controller should not access repository directly
// controller-> service-> repository -> DB
