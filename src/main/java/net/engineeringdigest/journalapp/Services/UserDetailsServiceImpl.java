package net.engineeringdigest.journalapp.Services;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalapp.Entity.User;
import net.engineeringdigest.journalapp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// this class connects: Spring security to MongoDB Users

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("Loading UserDetails for {}", username);

        User user = userService.findByUserName(username);

        if(user != null){

            log.info("User authenticated successfully; {}", username);

            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUserName())
                    .password(user.getPassword())
                    .roles(user.getRoles() != null ? user.getRoles().toArray(new String[0])
                    : new String[]{})
                    .build();
        }

        log.warn("User not found during authentication: {}", username);

        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
