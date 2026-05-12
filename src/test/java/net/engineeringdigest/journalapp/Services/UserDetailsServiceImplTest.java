package net.engineeringdigest.journalapp.Services;

import net.engineeringdigest.journalapp.Entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsernameTest(){

        User user = User.builder()
                .userName("Devansh")
                .password("Devansh")
                .roles(List.of("USER"))
                .build();

        when(userService.findByUserName("Devansh"))
                .thenReturn(user);

        UserDetails userDetails =
                userDetailsService.loadUserByUsername("Devansh");

        assertEquals("Devansh", userDetails.getUsername());
        assertEquals("Devansh", userDetails.getPassword());
    }
}