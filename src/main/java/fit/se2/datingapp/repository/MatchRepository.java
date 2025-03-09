package fit.se2.datingapp.repository;

import fit.se2.datingapp.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {
}