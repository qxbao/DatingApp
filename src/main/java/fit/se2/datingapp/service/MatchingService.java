package fit.se2.datingapp.service;

import fit.se2.datingapp.model.*;
import fit.se2.datingapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MatchingService {
    private final UserSwipeRepository swipeRepository;
    private final UserMatchRepository matchRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final UserReportRepository reportRepository;

    public boolean processSwipe(Long likerId, Long likedId, boolean isLike) {
        User liker = userRepository.findById(likerId).orElseThrow();
        User liked = userRepository.findById(likedId).orElseThrow();
        if (isLike) {
            UserSwipe swipe = UserSwipe.builder()
                    .liker(liker)
                    .liked(liked)
                    .isLike(true)
                    .likeDate(LocalDateTime.now())
                    .build();
            swipeRepository.save(swipe);
            Optional<UserSwipe> reverseLike = swipeRepository.findLikeSwipe(liked, liker);
            if (reverseLike.isPresent()) {
                UserMatch match = UserMatch.builder()
                        .user1(liker)
                        .user2(liked)
                        .matchDate(LocalDateTime.now())
                        .build();
                matchRepository.save(match);
                return true;
            }
        } else {
            UserSwipe swipe = UserSwipe.builder()
                    .liker(liker)
                    .liked(liked)
                    .isLike(false)
                    .likeDate(LocalDateTime.now())
                    .build();
            swipeRepository.save(swipe);
        }
        return false;
    }

    public UserProfile findNextProfileForUser(User user) {
        List<Long> swipedUserIds = swipeRepository.findSwipedUserIdsByUserId(user.getId());
        String preference = user.getPreference();
        List<String> preferences;
        if (preference.equals("both")) {
            preferences = List.of("male", "female");
        } else {
            preferences = List.of(preference);
        }
        return profileRepository.findNextProfileForUser(user.getId(), swipedUserIds, preferences, user.getGender());
    }
    public int getNumberOfALikeB(User b) {
        return (swipeRepository.findUserLikesByLiked(b)).size();
    }

    public List<UserMatch> getMatches(User user) {
        return matchRepository.findByUser1OrUser2(user, user);
    }
    public boolean isAMatchBetween(User a, User b) {
        return matchRepository.findByUser1AndUser2(a, b).isPresent() || matchRepository.findByUser1AndUser2(b, a).isPresent();
    }

    public boolean isSwiped(User a, User b) {
        return swipeRepository.findByLikerAndLiked(a, b).isPresent() || swipeRepository.findByLikerAndLiked(b, a).isPresent();
    }

    public void unmatch(User a, User b) {
        matchRepository.removeUserMatchByUser1AndUser2(a, b);
        matchRepository.removeUserMatchByUser1AndUser2(b, a);
    }

    public void reportUser(User a, User b, String reason) {
        UserReport report = UserReport.builder()
                .reporter(a)
                .reportedUser(b)
                .reason(reason)
                .isSolved(false)
                .time(LocalDateTime.now())
                .build();
        reportRepository.save(report);
    }
}