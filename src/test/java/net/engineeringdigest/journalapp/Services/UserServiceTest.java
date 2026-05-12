package net.engineeringdigest.journalapp.Services;

import net.engineeringdigest.journalapp.Entity.User;
import net.engineeringdigest.journalapp.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void saveNewUserTest(){

        User user = User.builder()
                .userName("Devansh")
                .password("Devansh")
                .build();

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encryptedPassword");

        userService.saveNewUser(user);

        verify(userRepository, times(1))
                .save(user);
    }
}