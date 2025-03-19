package fit.se2.datingapp.repository;

import fit.se2.datingapp.model.User;
import fit.se2.datingapp.model.UserLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserLikeRepository extends JpaRepository<UserLike, Long> {
    Optional<UserLike> findByLikerAndLiked(User liker, User liked);
    @Query("SELECT ul.liked.id FROM UserLike ul WHERE ul.liker.id = :userId")
    List<Long> findLikedUserIdsByUserId(Long userId);
    @Query("SELECT ul FROM UserLike ul WHERE ul.liked = :liked AND NOT EXISTS" +
            "(SELECT um FROM UserMatch um WHERE (um.user1 = ul.liker AND um.user2 = :liked)" +
            "OR (um.user1 = :liked AND um.user2 = ul.liker))")
    List<UserLike> findUserLikesByLiked(User liked);
}