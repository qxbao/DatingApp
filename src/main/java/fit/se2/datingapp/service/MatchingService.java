package fit.se2.datingapp.service;

import fit.se2.datingapp.model.User;
import fit.se2.datingapp.model.UserLike;
import fit.se2.datingapp.model.UserMatch;
import fit.se2.datingapp.model.UserProfile;
import fit.se2.datingapp.repository.UserLikeRepository;
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

    @Autowired
    private UserLikeRepository likeRepository;

    @Autowired
    private UserMatchRepository matchRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    public boolean processSwipe(Long likerId, Long likedId, boolean isLike) {
        User liker = userRepository.findById(likerId).orElseThrow();
        User liked = userRepository.findById(likedId).orElseThrow();
        if (isLike) {
            // Save the like action
            UserLike like = UserLike.builder()
                    .liker(liker)
                    .liked(liked)
                    .likeDate(LocalDateTime.now())
                    .build();

            likeRepository.save(like);

            // Check if the other user also liked this user
            Optional<UserLike> reverseLike = likeRepository.findByLikerAndLiked(liked, liker);

            if (reverseLike.isPresent()) {
                // It's a match! Create a match record
                UserMatch match = UserMatch.builder()
                        .user1(liker)
                        .user2(liked)
                        .matchDate(LocalDateTime.now())
                        .build();

                matchRepository.save(match);
                return true;
            }
        }
        // No need to save dislike records

        return false;
    }

    public UserProfile findNextProfileForUser(User user) {
        // Get user ids that current user has already liked
        List<Long> likedUserIds = likeRepository.findLikedUserIdsByUserId(user.getId());
        String preference = user.getPreference();
        List<String> preferences;
        if (preference.equals("both")) {
            preferences = List.of("male", "female");
        } else {
            preferences = List.of(preference);
        }
        // Get a potential match based on preferences and filters
        return profileRepository.findNextProfileForUser(user.getId(), likedUserIds, preferences, user.getGender());
    }
}