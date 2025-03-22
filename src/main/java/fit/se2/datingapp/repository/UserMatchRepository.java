package fit.se2.datingapp.repository;

import fit.se2.datingapp.model.User;
import fit.se2.datingapp.model.UserMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMatchRepository extends JpaRepository<UserMatch, Long> {
    List<UserMatch> findByUser1OrUser2(User user1, User user2);

    Optional<UserMatch> findByUser1AndUser2(User a, User b);
    @Transactional
    void removeUserMatchByUser1AndUser2(User a, User b);
}