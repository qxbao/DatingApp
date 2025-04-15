package fit.se2.datingapp.controller;

import fit.se2.datingapp.dto.CreateAccountRequestDTO;
import fit.se2.datingapp.dto.CreateAccountResponseDTO;
import fit.se2.datingapp.model.User;
import fit.se2.datingapp.service.ProfileService;
import fit.se2.datingapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private CreateAccountRequestDTO buildValidRequest() {
        CreateAccountRequestDTO dto = new CreateAccountRequestDTO();
        dto.setName("Nam Vu");
        dto.setEmail("namvu369@gmail.com");
        dto.setDob("2004-04-12");
        dto.setGender("MALE");
        dto.setPreference("FEMALE");
        dto.setPassword("12345678");
        return dto;
    }

    @Test
    public void testRegister_Success() {
        CreateAccountRequestDTO requestDTO = buildValidRequest();

        when(profileService.getAge(LocalDate.parse(requestDTO.getDob()))).thenReturn(21);
        when(userService.isEmailExist(requestDTO.getEmail())).thenReturn(false);

        ResponseEntity<CreateAccountResponseDTO> response = userController.register(requestDTO);

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isError());
        assertNull(response.getBody().getErrorMessage());

        verify(userService, times(1)).create(any(User.class));
    }

    @Test
    public void testRegister_NameTooShort() {
        CreateAccountRequestDTO requestDTO = buildValidRequest();
        requestDTO.setName("Na");

        ResponseEntity<CreateAccountResponseDTO> response = userController.register(requestDTO);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().isError());
        assertEquals("Name must be at least 3 characters long", response.getBody().getErrorMessage());
        verify(userService, never()).create(any(User.class));
    }

    @Test
    public void testRegister_PasswordTooShort() {
        CreateAccountRequestDTO requestDTO = buildValidRequest();
        requestDTO.setPassword("123");

        ResponseEntity<CreateAccountResponseDTO> response = userController.register(requestDTO);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().isError());
        assertEquals("Password must be at least 8 characters long", response.getBody().getErrorMessage());
        verify(userService, never()).create(any(User.class));
    }

    @Test
    public void testRegister_UserUnder18() {
        CreateAccountRequestDTO requestDTO = buildValidRequest();
        requestDTO.setDob("2010-04-30");

        when(profileService.getAge(LocalDate.parse("2010-04-30"))).thenReturn(14);

        ResponseEntity<CreateAccountResponseDTO> response = userController.register(requestDTO);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().isError());
        assertEquals("User must be at least 18 years old", response.getBody().getErrorMessage());
        verify(userService, never()).create(any(User.class));
    }

    @Test
    public void testRegister_EmailAlreadyExists() {
        CreateAccountRequestDTO requestDTO = buildValidRequest();

        when(profileService.getAge(LocalDate.parse(requestDTO.getDob()))).thenReturn(21);
        when(userService.isEmailExist(requestDTO.getEmail())).thenReturn(true);

        ResponseEntity<CreateAccountResponseDTO> response = userController.register(requestDTO);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().isError());
        assertEquals("Email already exists", response.getBody().getErrorMessage());
        verify(userService, never()).create(any(User.class));
    }

    @Test
    public void testRegister_MissingFields() {
        CreateAccountRequestDTO requestDTO = new CreateAccountRequestDTO();

        ResponseEntity<CreateAccountResponseDTO> response = userController.register(requestDTO);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().isError());
        assertEquals("All fields are required", response.getBody().getErrorMessage());
        verify(userService, never()).create(any(User.class));
    }

    @Test
    public void testRegister_CreateThrowsException() {
        CreateAccountRequestDTO requestDTO = buildValidRequest();

        when(profileService.getAge(LocalDate.parse(requestDTO.getDob()))).thenReturn(25);
        when(userService.isEmailExist(requestDTO.getEmail())).thenReturn(false);
        doThrow(new RuntimeException("DB error")).when(userService).create(any(User.class));

        ResponseEntity<CreateAccountResponseDTO> response = userController.register(requestDTO);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().isError());
        assertEquals("Error creating account", response.getBody().getErrorMessage());
    }
}
