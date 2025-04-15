package fit.se2.datingapp.service;

import fit.se2.datingapp.model.User;
import fit.se2.datingapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .id(1L)
                .name("Nam Vu")
                .dob(LocalDate.of(2004, 4, 12))
                .email("namvu369@example.com")
                .role("USER")
                .gender("MALE")
                .preference("FEMALE")
                .password("12345678")
                .build();
    }

    @Test
    public void testLoadUserByUsername_Found() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        User result = (User) userService.loadUserByUsername(user.getEmail());

        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    public void testLoadUserByUsername_NotFound() {
        when(userRepository.findByEmail("notfound@gmail.com")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("notfound@gmail.com"));
    }

    @Test
    public void testGetUserById_Found() {
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
    }

    @Test
    public void testGetUserById_NotFound() {
        when(userRepository.findById(2L)).thenReturn(java.util.Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.getUserById(2L));
    }

    @Test
    public void testCreate_ReturnsSuccessMessage() {
        String result = userService.create(user);

        assertEquals("Create Successfully!", result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testIsEmailExist_True() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        assertTrue(userService.isEmailExist(user.getEmail()));
    }

    @Test
    public void testIsEmailExist_False() {
        when(userRepository.findByEmail("unknown@gmail.com")).thenReturn(null);

        assertFalse(userService.isEmailExist("unknown@gmail.com"));
    }

    @Test
    public void testIsAuth_True() {
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertTrue(UserService.isAuth());
    }

    @Test
    public void testIsAuth_False() {
        SecurityContextHolder.clearContext();
        assertFalse(UserService.isAuth());
    }

    @Test
    public void testGetCurrentUser_Authenticated() {
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        User result = UserService.getCurrentUser();

        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    public void testGetCurrentUser_NotAuthenticated() {
        SecurityContextHolder.clearContext();

        User result = UserService.getCurrentUser();

        assertNull(result);
    }
}
