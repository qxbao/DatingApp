package fit.se2.datingapp.service;

import fit.se2.datingapp.model.Message;
import fit.se2.datingapp.model.User;
import fit.se2.datingapp.model.UserReport;
import fit.se2.datingapp.repository.MessageRepository;
import fit.se2.datingapp.repository.UserReportRepository;
import fit.se2.datingapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReportServiceTest {

    @Mock private UserReportRepository reportRepository;
    @Mock private MessageRepository chatMessageRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private ReportService reportService;

    private UserReport report;
    private User reportedUser;
    private User admin;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        User reporter = User.builder().id(1L).name("Alice").role("USER").build();
        reportedUser = User.builder().id(2L).name("Bob").role("USER").build();
        admin = User.builder().id(3L).name("Admin").role("ADMIN").build();

        report = UserReport.builder()
                .id(100L)
                .reporter(reporter)
                .reportedUser(reportedUser)
                .isSolved(false)
                .reason("Violence")
                .build();
    }

    @Test
    public void testFindAllUnsolved() {
        List<UserReport> reports = List.of(report);
        when(reportRepository.findByIsSolved(false)).thenReturn(reports);

        List<UserReport> result = reportService.findAllUnsolved();

        assertEquals(1, result.size());
        assertFalse(result.getFirst().isSolved());
        verify(reportRepository).findByIsSolved(false);
    }

    @Test
    public void testFindById_Found() {
        when(reportRepository.findById(100L)).thenReturn(Optional.of(report));

        UserReport result = reportService.findById(100L);

        assertNotNull(result);
        assertEquals(100L, result.getId());
    }

    @Test
    public void testFindById_NotFound() {
        when(reportRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> reportService.findById(999L));
    }

    @Test
    public void testGetChatLogsBetweenUsers() {
        List<Message> messages = List.of(
                Message.builder().id(1L).content("Hello").build(),
                Message.builder().id(2L).content("Hi").build()
        );
        when(chatMessageRepository.findMessagesBetweenUsers(1L, 2L)).thenReturn(messages);

        List<Message> result = reportService.getChatLogsBetweenUsers(1L, 2L);

        assertEquals(2, result.size());
    }

    @Test
    public void testResolveReport_WithBan() {
        when(reportRepository.findById(100L)).thenReturn(Optional.of(report));

        reportService.resolveReport(100L, admin, true, "User violated rules");

        assertTrue(report.isSolved());
        assertEquals("BANNED", reportedUser.getRole());
        assertEquals(admin, report.getSolver());
        assertEquals("User violated rules", report.getSolvingDescription());

        verify(userRepository).save(reportedUser);
        verify(reportRepository).save(report);
    }

    @Test
    public void testResolveReport_WithoutBan() {
        when(reportRepository.findById(100L)).thenReturn(Optional.of(report));

        reportService.resolveReport(100L, admin, false, "No serious issue");

        assertTrue(report.isSolved());
        assertEquals("USER", reportedUser.getRole());
        assertEquals(admin, report.getSolver());
        assertEquals("No serious issue", report.getSolvingDescription());

        verify(userRepository, never()).save(reportedUser);
        verify(reportRepository).save(report);
    }

    @Test
    public void testFindAll() {
        List<UserReport> reports = List.of(report);
        when(reportRepository.findAll()).thenReturn(reports);

        List<UserReport> result = reportService.findAll();

        assertEquals(1, result.size());
    }

    @Test
    public void testFindAllResolved() {
        UserReport resolvedReport = UserReport.builder().id(101L).isSolved(true).build();
        when(reportRepository.findByIsSolved(true)).thenReturn(List.of(resolvedReport));

        List<UserReport> result = reportService.findAllResolved();

        assertEquals(1, result.size());
        assertTrue(result.getFirst().isSolved());
    }
}
