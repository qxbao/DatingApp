package fit.se2.datingapp.controller;

import fit.se2.datingapp.model.User;
import fit.se2.datingapp.service.ProfileService;
import fit.se2.datingapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class IndexControllerTest {

    private ProfileService profileService;
    private IndexController indexController;

    @BeforeEach
    public void setUp() {
        profileService = mock(ProfileService.class);
        indexController = new IndexController(profileService);
    }

    @Test
    public void testIndex_UserIsNull() {
        try (MockedStatic<UserService> mockedUserService = mockStatic(UserService.class)) {
            mockedUserService.when(UserService::getCurrentUser).thenReturn(null);
            Model model = mock(Model.class);

            String viewName = indexController.index(model);

            verify(model).addAttribute(eq("new_user"), any(User.class));
            assertEquals("index", viewName);
        }
    }

    @Test
    public void testIndex_UserIsBanned() {
        try (MockedStatic<UserService> mockedUserService = mockStatic(UserService.class)) {
            User user = new User();
            user.setRole("BANNED");
            mockedUserService.when(UserService::getCurrentUser).thenReturn(user);

            String viewName = indexController.index(mock(Model.class));

            assertEquals("redirect:/banned", viewName);
        }
    }

    @Test
    public void testIndex_ProfileExists() {
        try (MockedStatic<UserService> mockedUserService = mockStatic(UserService.class)) {
            User user = new User();
            user.setRole("USER");
            mockedUserService.when(UserService::getCurrentUser).thenReturn(user);
            when(profileService.isProfileExist(user)).thenReturn(true);

            String viewName = indexController.index(mock(Model.class));

            assertEquals("app", viewName);
        }
    }

    @Test
    public void testIndex_ProfileDoesNotExist() {
        try (MockedStatic<UserService> mockedUserService = mockStatic(UserService.class)) {
            User user = new User();
            user.setRole("USER");
            mockedUserService.when(UserService::getCurrentUser).thenReturn(user);
            when(profileService.isProfileExist(user)).thenReturn(false);

            String viewName = indexController.index(mock(Model.class));

            assertEquals("redirect:/profile/init", viewName);
        }
    }

    @Test
    public void testBannedPage() {
        String viewName = indexController.bannedPage();
        assertEquals("banned", viewName);
    }
}
