package fit.se2.datingapp.service;

import fit.se2.datingapp.model.*;
import fit.se2.datingapp.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MatchingServiceTest {

    @Mock private UserSwipeRepository swipeRepository;
    @Mock private UserMatchRepository matchRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProfileRepository profileRepository;
    @Mock private UserReportRepository reportRepository;

    @InjectMocks private MatchingService matchingService;

    private User userA;
    private User userB;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userA = User.builder().id(1L).name("Nam Vu").gender("MALE").preference("FEMALE").build();
        userB = User.builder().id(2L).name("Le Thanh Thu").gender("FEMALE").preference("MALE").build();
    }

    @Test
    public void testProcessSwipe_MatchSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userA));
        when(userRepository.findById(2L)).thenReturn(Optional.of(userB));
        when(swipeRepository.findLikeSwipe(userB, userA)).thenReturn(Optional.of(UserSwipe.builder().build()));

        boolean result = matchingService.processSwipe(1L, 2L, true);

        assertTrue(result);
        verify(swipeRepository).save(any(UserSwipe.class));
        verify(matchRepository).save(any(UserMatch.class));
    }

    @Test
    public void testProcessSwipe_NotMatch() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userA));
        when(userRepository.findById(2L)).thenReturn(Optional.of(userB));
        when(swipeRepository.findLikeSwipe(userB, userA)).thenReturn(Optional.empty());

        boolean result = matchingService.processSwipe(1L, 2L, true);

        assertFalse(result);
        verify(swipeRepository).save(any(UserSwipe.class));
        verify(matchRepository, never()).save(any(UserMatch.class));
    }

    @Test
    public void testFindNextProfileForUser() {
        List<Long> swipedIds = List.of(3L, 4L);
        when(swipeRepository.findSwipedUserIdsByUserId(userA.getId())).thenReturn(swipedIds);
        when(profileRepository.findNextProfileForUser(eq(userA.getId()), eq(swipedIds), anyList(), eq("MALE")))
                .thenReturn(UserProfile.builder().id(5L).build());

        UserProfile nextProfile = matchingService.findNextProfileForUser(userA);

        assertNotNull(nextProfile);
    }

    @Test
    public void testGetNumberOfALikeB() {
        List<UserSwipe> likes = List.of(new UserSwipe(), new UserSwipe());
        when(swipeRepository.findUserLikesByLiked(userB)).thenReturn(likes);

        int result = matchingService.getNumberOfALikeB(userB);

        assertEquals(2, result);
    }

    @Test
    public void testGetMatches() {
        List<UserMatch> matches = List.of(UserMatch.builder().user1(userA).user2(userB).build());
        when(matchRepository.findByUser1OrUser2(userA, userA)).thenReturn(matches);

        List<UserMatch> result = matchingService.getMatches(userA);

        assertEquals(1, result.size());
    }

    @Test
    public void testIsAMatchBetween() {
        when(matchRepository.findByUser1AndUser2(userA, userB)).thenReturn(Optional.of(UserMatch.builder().build()));

        boolean result = matchingService.isAMatchBetween(userA, userB);

        assertTrue(result);
    }

    @Test
    public void testIsSwiped() {
        when(swipeRepository.findByLikerAndLiked(userA, userB)).thenReturn(Optional.of(UserSwipe.builder().build()));

        boolean result = matchingService.isSwiped(userA, userB);

        assertTrue(result);
    }

    @Test
    public void testUnmatch() {
        matchingService.unmatch(userA, userB);

        verify(matchRepository).removeUserMatchByUser1AndUser2(userA, userB);
        verify(matchRepository).removeUserMatchByUser1AndUser2(userB, userA);
    }

    @Test
    public void testReportUser() {
        matchingService.reportUser(userA, userB, "Fake profile");

        verify(reportRepository).save(any(UserReport.class));
    }
}
