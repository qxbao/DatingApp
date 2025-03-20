package fit.se2.datingapp.repository;

import fit.se2.datingapp.model.User;
import fit.se2.datingapp.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileRepository extends JpaRepository<UserProfile, Long> {
    @Query(value = "SELECT up FROM UserProfile up " +
            "WHERE up.user.id != :userId " +
            "AND up.user.id NOT IN :swipedUserIds " +
            "AND up.user.gender IN :preferences " +
            "AND up.user.preference IN (:gender, 'both') " +
            "AND NOT EXISTS (SELECT us FROM UserSwipe us WHERE us.liked.id = :userId " +
            "AND us.liker = up.user AND us.isLike = false) " +
            "ORDER BY FUNCTION('RAND') LIMIT 1")
    UserProfile findNextProfileForUser(Long userId, List<Long> swipedUserIds, List<String> preferences, String gender);
    UserProfile findByUser(User user);
}