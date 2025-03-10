package fit.se2.datingapp.repository;

import fit.se2.datingapp.model.UserLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLikeRepository extends JpaRepository<UserLike, Long> {
}