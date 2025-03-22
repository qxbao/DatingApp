package fit.se2.datingapp.repository;

import fit.se2.datingapp.model.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserReportRepository extends JpaRepository<UserReport, Long> {
    List<UserReport> findByIsSolved(boolean isSolved);
}