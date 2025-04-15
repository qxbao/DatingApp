package fit.se2.datingapp.controller;

import fit.se2.datingapp.dto.*;
import fit.se2.datingapp.model.*;
import fit.se2.datingapp.service.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MatchingControllerTest {

    @Mock private ProfileService profileService;
    @Mock private UserPhotoService photoService;
    @Mock private MatchingService matchingService;
    @Mock private UserService userService;
    private MatchingController controller;

    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        controller = new MatchingController(profileService, photoService, matchingService, userService);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testHandleSwipe_MatchSuccess() {
        try (MockedStatic<UserService> mockedStatic = mockStatic(UserService.class)) {
            User currentUser = User.builder().id(1L).build();
            mockedStatic.when(UserService::getCurrentUser).thenReturn(currentUser);

            SwipeRequestDTO swipeRequest = new SwipeRequestDTO();
            swipeRequest.setTargetUserId(2L);
            swipeRequest.setLike(true);

            when(matchingService.processSwipe(1L, 2L, true)).thenReturn(true);

            ResponseEntity<SwipeResponseDTO> response = controller.handleSwipe(swipeRequest);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().isMatch());
            assertEquals("You have a new match!", response.getBody().getMessage());
        }
    }

    @Test
    public void testGetNextProfile_NoProfileFound() {
        try (MockedStatic<UserService> mockedStatic = mockStatic(UserService.class)) {
            User currentUser = User.builder().id(1L).build();
            mockedStatic.when(UserService::getCurrentUser).thenReturn(currentUser);

            when(matchingService.findNextProfileForUser(currentUser)).thenReturn(null);

            ResponseEntity<ProfileDTO> response = controller.getNextProfile();

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }
    }

    @Test
    public void testGetNextProfile_ProfileFound() {
        try (MockedStatic<UserService> mockedStatic = mockStatic(UserService.class)) {
            User user = User.builder().id(2L).name("Nam Vu").dob(LocalDate.of(2004, 4, 12)).build();
            UserProfile profile = UserProfile.builder().user(user)
                    .bio("Hello")
                    .education("HANU")
                    .occupation("Student")
                    .height("165")
                    .religion("Atheism").build();

            mockedStatic.when(UserService::getCurrentUser).thenReturn(User.builder().id(1L).build());

            when(matchingService.findNextProfileForUser(any())).thenReturn(profile);
            when(photoService.getUserPhotos(user)).thenReturn(List.of(UserPhoto.builder().photoUrl("url1").build()));
            when(profileService.getAge(user.getDob())).thenReturn(21);

            ResponseEntity<ProfileDTO> response = controller.getNextProfile();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("Nam Vu", response.getBody().getName());
            assertEquals("url1", response.getBody().getPhotoUrls().getFirst());
        }
    }

    @Test
    public void testGetActiveConversations_Success() {
        User currentUser = User.builder().id(1L).name("Nam Vu").build();
        User matchUser1 = User.builder().id(2L).name("Le Thanh Thu").build();
        User matchUser2 = User.builder().id(3L).name("Nguyen Kim Dinh").build();

        UserMatch match1 = UserMatch.builder().user1(currentUser).user2(matchUser1).build();
        UserMatch match2 = UserMatch.builder().user1(currentUser).user2(matchUser2).build();

        try (MockedStatic<UserService> mockedStatic = mockStatic(UserService.class)) {
            mockedStatic.when(UserService::getCurrentUser).thenReturn(currentUser);

            when(matchingService.getMatches(currentUser)).thenReturn(List.of(match1, match2));
            when(userService.getUserById(2L)).thenReturn(matchUser1);
            when(userService.getUserById(3L)).thenReturn(matchUser2);
            when(photoService.getUserAvatar(matchUser1)).thenReturn(null);
            when(photoService.getUserAvatar(matchUser2)).thenReturn(null);

            ResponseEntity<ActiveConversationDTO> response = controller.getActiveConversations();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(2, response.getBody().getLen());

            assertEquals("Le Thanh Thu", response.getBody().getNames().get(0));
            assertEquals("Nguyen Kim Dinh", response.getBody().getNames().get(1));

            assertEquals("Click to enter chat", response.getBody().getLastMessages().get(0));
            assertEquals("Click to enter chat", response.getBody().getLastMessages().get(1));

            assertEquals(2L, response.getBody().getIds().get(0));
            assertEquals(3L, response.getBody().getIds().get(1));
        }
    }

    @Test
    public void testGetActiveConversations_UserNotFound() {
        try (MockedStatic<UserService> mockedStatic = mockStatic(UserService.class)) {
            mockedStatic.when(UserService::getCurrentUser).thenReturn(null);

            ResponseEntity<ActiveConversationDTO> response = controller.getActiveConversations();

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Test
    public void testGetActiveConversations_NoMatches() {
        User currentUser = User.builder().id(1L).name("Nam Vu").build();
        try (MockedStatic<UserService> mockedStatic = mockStatic(UserService.class)) {
            mockedStatic.when(UserService::getCurrentUser).thenReturn(currentUser);

            when(matchingService.getMatches(currentUser)).thenReturn(List.of());

            ResponseEntity<ActiveConversationDTO> response = controller.getActiveConversations();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(0, response.getBody().getLen());
        }
    }


    @Test
    public void testUnMatch_Success() {
        User user = User.builder().id(1L).build();
        Long targetId = 2L;
        User targetUser = User.builder().id(targetId).build();

        when(userService.getUserById(targetId)).thenReturn(targetUser);

        ResponseEntity<String> response = controller.unmatch(user, targetId);

        verify(matchingService, times(1)).unmatch(user, targetUser);
        assertEquals("Ok", response.getBody());
    }

    @Test
    public void testReportUser_NotMatched() {
        User user = User.builder().id(1L).build();
        UserReportDTO report = new UserReportDTO();
        report.setReportedUserId(2L);
        report.setReason("Spam");

        when(userService.getUserById(2L)).thenReturn(User.builder().id(2L).build());
        when(matchingService.isAMatchBetween(any(), any())).thenReturn(false);

        ResponseEntity<String> response = controller.reportUser(report, user);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Error"));
    }
}
