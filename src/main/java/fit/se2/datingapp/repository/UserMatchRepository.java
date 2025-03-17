package fit.se2.datingapp.repository;

import fit.se2.datingapp.model.User;
import fit.se2.datingapp.model.UserMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMatchRepository extends JpaRepository<UserMatch, Long> {
    List<UserMatch> findByUser1OrUser2(User user1, User user2);
}