package fit.se2.datingapp.service;

import fit.se2.datingapp.model.Message;
import fit.se2.datingapp.model.User;
import fit.se2.datingapp.model.UserReport;
import fit.se2.datingapp.repository.MessageRepository;
import fit.se2.datingapp.repository.UserReportRepository;
import fit.se2.datingapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final UserReportRepository reportRepository;
    private final MessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public List<UserReport> findAllUnsolved() {
        return reportRepository.findByIsSolved(false);
    }

    public UserReport findById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
    }

    public List<Message> getChatLogsBetweenUsers(Long user1Id, Long user2Id) {
        return chatMessageRepository.findMessagesBetweenUsers(user1Id, user2Id);
    }

    @Transactional
    public void resolveReport(Long reportId, User admin, boolean banUser, String description) {
        UserReport report = findById(reportId);
        report.setSolved(true);
        report.setSolver(admin);
        report.setSolvingDescription(description);
        report.setSolvedTime(LocalDateTime.now());
        if (banUser) {
            User reportedUser = report.getReportedUser();
            reportedUser.setRole("BANNED");
            userRepository.save(reportedUser);
        }

        reportRepository.save(report);
    }

    public List<UserReport> findAll() {
        return reportRepository.findAll();
    }

    public List<UserReport> findAllResolved() {
        return reportRepository.findByIsSolved(true);
    }
}