package fit.se2.datingapp.controller;

import fit.se2.datingapp.dto.AddPhotoDTO;
import fit.se2.datingapp.dto.RemovePhotoDTO;
import fit.se2.datingapp.model.*;
import fit.se2.datingapp.service.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProfileControllerTest {

    @Mock private ProfileService profileService;
    @Mock private UserPhotoService userPhotoService;
    @Mock private MatchingService matchingService;
    @Mock private UserService userService;
    @Mock private Model model;

    private ProfileController controller;
    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        controller = new ProfileController(profileService, userPhotoService, matchingService, userService);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testCreateProfilePage_ProfileNotExist() {
        try (MockedStatic<UserService> mockedStatic = mockStatic(UserService.class)) {
            User user = User.builder().id(1L).build();
            mockedStatic.when(UserService::getCurrentUser).thenReturn(user);

            when(profileService.isProfileExist(user)).thenReturn(false);

            String view = controller.createProfilePage(model);

            assertEquals("profile/init", view);
            verify(model).addAttribute(eq("profile"), any(UserProfile.class));
            verify(model).addAttribute("user", user);
        }
    }

    @Test
    public void testCreateProfilePage_ProfileExists() {
        try (MockedStatic<UserService> mockedStatic = mockStatic(UserService.class)) {
            User user = User.builder().id(1L).build();
            mockedStatic.when(UserService::getCurrentUser).thenReturn(user);

            when(profileService.isProfileExist(user)).thenReturn(true);

            String view = controller.createProfilePage(model);
            assertEquals("redirect:/", view);
        }
    }

    @Test
    public void testUpdateProfilePage_ProfileNotExist() {
        try (MockedStatic<UserService> mockedStatic = mockStatic(UserService.class)) {
            User user = User.builder().id(1L).build();
            mockedStatic.when(UserService::getCurrentUser).thenReturn(user);

            when(profileService.isProfileExist(user)).thenReturn(false);
            String view = controller.updateProfilePage(model);
            assertEquals("redirect:/profile/init", view);
        }
    }

    @Test
    public void testUpdateProfilePage_ProfileExists() {
        try (MockedStatic<UserService> mockedStatic = mockStatic(UserService.class)) {
            User user = User.builder().id(1L).build();
            mockedStatic.when(UserService::getCurrentUser).thenReturn(user);

            UserProfile profile = UserProfile.builder().bio("test").build();
            when(profileService.isProfileExist(user)).thenReturn(true);
            when(profileService.findByUser(user)).thenReturn(profile);
            when(userPhotoService.getUserPhotos(user)).thenReturn(List.of());

            String view = controller.updateProfilePage(model);
            assertEquals("profile/update", view);
            verify(model).addAttribute("photos", List.of());
            verify(model).addAttribute("profile", profile);
        }
    }

    @Test
    public void testViewProfilePage_AsMatchOrAdmin() {
        User user = User.builder().id(1L).role("USER").build();
        User target = User.builder().id(2L).dob(LocalDate.of(2004, 3, 25)).build();
        UserProfile profile = UserProfile.builder().user(target).build();
        when(userService.getUserById(2L)).thenReturn(target);
        when(matchingService.isAMatchBetween(user, target)).thenReturn(true);
        when(profileService.findByUser(target)).thenReturn(profile);
        when(userPhotoService.getUserPhotos(target)).thenReturn(List.of(
                UserPhoto.builder().photoUrl("url").build()
        ));
        when(profileService.getAge(target.getDob())).thenReturn(21);

        String view = controller.viewProfilePage(model, 2L, user);

        assertEquals("profile/view", view);
        verify(model).addAttribute("photos", List.of("\"url\""));
        verify(model).addAttribute("profile", profile);
        verify(model).addAttribute("targetUser", target);
        verify(model).addAttribute("age", 21);
    }

    @Test
    public void testViewProfilePage_Unauthorized() {
        User user = User.builder().id(1L).role("USER").build();
        User target = User.builder().id(2L).build();
        when(userService.getUserById(2L)).thenReturn(target);
        when(matchingService.isAMatchBetween(user, target)).thenReturn(false);

        String view = controller.viewProfilePage(model, 2L, user);
        assertEquals("redirect:/", view);
    }

    @Test
    public void testCreateProfile() {
        try (MockedStatic<UserService> mockedStatic = mockStatic(UserService.class)) {
            User user = User.builder().id(1L).build();
            mockedStatic.when(UserService::getCurrentUser).thenReturn(user);

            UserProfile profile = new UserProfile();
            String result = controller.createProfile(profile);

            verify(profileService).create(profile);
            assertEquals(user, profile.getUser());
            assertEquals("redirect:/profile/update", result);
        }
    }

    @Test
    public void testUpdateProfile() {
        UserProfile profile = new UserProfile();
        String result = controller.updateProfile(profile);

        verify(profileService).create(profile);
        assertEquals("redirect:/profile/update", result);
    }

    @Test
    public void testAddPhoto_ValidUrl() {
        try (MockedStatic<UserService> mockedStatic = mockStatic(UserService.class)) {
            User user = User.builder().id(1L).build();
            mockedStatic.when(UserService::getCurrentUser).thenReturn(user);

            AddPhotoDTO dto = new AddPhotoDTO();
            ReflectionTestUtils.setField(dto, "url", "https://photo.com/image.png");

            ResponseEntity<HttpStatus> response = controller.addPhoto(dto);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Test
    public void testAddPhoto_InvalidUrl() {
        AddPhotoDTO dto = new AddPhotoDTO();

        ResponseEntity<HttpStatus> response = controller.addPhoto(dto);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testRemovePhoto() {
        RemovePhotoDTO dto = new RemovePhotoDTO();
        dto.setId(1L);

        ResponseEntity<HttpStatus> response = controller.removePhoto(dto);

        verify(userPhotoService).removePhotoById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
