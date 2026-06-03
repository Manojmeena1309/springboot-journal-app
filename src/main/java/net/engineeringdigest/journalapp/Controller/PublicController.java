package net.engineeringdigest.journalapp.Controller;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalapp.Entity.User;
import net.engineeringdigest.journalapp.Services.UserDetailsServiceImpl;
import net.engineeringdigest.journalapp.Services.UserService;
import net.engineeringdigest.journalapp.utillis.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @GetMapping("/health-check")  // Is the Application Alive and working?
    public String healthCheck(){
        return "OK";
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user){

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

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user){
      try{

          authenticationManager.authenticate(
                  new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));
          UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());
          String jwt = jwtUtil.generateToken(userDetails.getUsername());
          return new ResponseEntity<>(jwt, HttpStatus.OK);
      }catch (Exception e){
          log.error("Exception occurred while createAuthenticationToken", e);
          return new ResponseEntity<>("Incorrect username or password", HttpStatus.BAD_REQUEST);
      }
    }
}
