package fit.se2.datingapp.repository;

import fit.se2.datingapp.model.User;
import fit.se2.datingapp.model.UserSwipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSwipeRepository extends JpaRepository<UserSwipe, Long> {
    @Query("SELECT us FROM UserSwipe us WHERE us.liker = :liker AND us.liked = :liked AND us.isLike = true")
    Optional<UserSwipe> findLikeSwipe(User liker, User liked);
    @Query("SELECT us.liked.id FROM UserSwipe us WHERE us.liker.id = :userId")
    List<Long> findSwipedUserIdsByUserId(Long userId);
    @Query("SELECT us FROM UserSwipe us WHERE us.liked = :liked AND us.isLike = true AND NOT EXISTS" +
            "(SELECT um FROM UserMatch um WHERE (um.user1 = us.liker AND um.user2 = :liked)" +
            "OR (um.user1 = :liked AND um.user2 = us.liker))")
    List<UserSwipe> findUserLikesByLiked(User liked);

    Optional<Object> findByLikerAndLiked(User a, User b);
}