package fit.se2.datingapp.service;

import fit.se2.datingapp.model.User;
import fit.se2.datingapp.model.UserSwipe;
import fit.se2.datingapp.model.UserMatch;
import fit.se2.datingapp.model.UserProfile;
import fit.se2.datingapp.repository.UserSwipeRepository;
import fit.se2.datingapp.repository.UserMatchRepository;
import fit.se2.datingapp.repository.ProfileRepository;
import fit.se2.datingapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MatchingService {
    private final UserSwipeRepository swipeRepository;
    private final UserMatchRepository matchRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    @Autowired
    public MatchingService(UserSwipeRepository swipeRepository, UserMatchRepository matchRepository, UserRepository userRepository, ProfileRepository profileRepository) {
        this.swipeRepository = swipeRepository;
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

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
}