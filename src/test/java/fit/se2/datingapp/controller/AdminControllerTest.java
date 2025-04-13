package fit.se2.datingapp.controller;

import fit.se2.datingapp.model.Message;
import fit.se2.datingapp.model.User;
import fit.se2.datingapp.model.UserReport;
import fit.se2.datingapp.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AdminControllerTest {

    private AdminController adminController;
    private ReportService reportService;

    @BeforeEach
    public void setUp() {
        reportService = mock(ReportService.class);
        adminController = new AdminController(reportService);
    }

    @Test
    public void testDashboard_DefaultFilterUnsolved() {
        Model model = mock(Model.class);
        List<UserReport> dummyReports = List.of();
        when(reportService.findAllUnsolved()).thenReturn(dummyReports);

        String viewName = adminController.dashboard("unsolved", model);

        verify(model).addAttribute("reports", dummyReports);
        verify(model).addAttribute("currentFilter", "unsolved");
        assertEquals("admin/dashboard", viewName);
    }

    @Test
    public void testDashboard_FilterResolved() {
        Model model = mock(Model.class);
        List<UserReport> dummyReports = List.of();
        when(reportService.findAllResolved()).thenReturn(dummyReports);

        String viewName = adminController.dashboard("resolved", model);

        verify(model).addAttribute("reports", dummyReports);
        verify(model).addAttribute("currentFilter", "resolved");
        assertEquals("admin/dashboard", viewName);
    }

    @Test
    public void testDashboard_FilterAll() {
        Model model = mock(Model.class);
        List<UserReport> dummyReports = List.of();
        when(reportService.findAll()).thenReturn(dummyReports);

        String viewName = adminController.dashboard("all", model);

        verify(model).addAttribute("reports", dummyReports);
        verify(model).addAttribute("currentFilter", "all");
        assertEquals("admin/dashboard", viewName);
    }

    @Test
    public void testViewReport() {
        Long reportId = 1L;
        Model model = mock(Model.class);
        UserReport mockReport = mock(UserReport.class);

        when(reportService.findById(reportId)).thenReturn(mockReport);

        String viewName = adminController.viewReport(reportId, model, null);

        verify(reportService).findById(reportId);
        verify(model).addAttribute("report", mockReport);
        assertEquals("admin/report-detail", viewName);
    }

    @Test
    public void testViewChatLogs() {
        Long reportId = 1L;
        Model model = mock(Model.class);

        User mockReporter = mock(User.class);
        User mockReportedUser = mock(User.class);

        when(mockReporter.getId()).thenReturn(3L);
        when(mockReportedUser.getId()).thenReturn(6L);

        UserReport mockReport = mock(UserReport.class);
        when(mockReport.getReporter()).thenReturn(mockReporter);
        when(mockReport.getReportedUser()).thenReturn(mockReportedUser);

        List<Message> mockChatLogs = new ArrayList<>();
        mockChatLogs.add(
                Message.builder()
                        .id(1L)
                        .content("Message content")
                        .sender(mockReportedUser)  // ID 6
                        .receiver(mockReporter)    // ID 3
                        .date(LocalDateTime.now())
                        .build()
        );
        mockChatLogs.add(
                Message.builder()
                        .id(2L)
                        .content("Another message")
                        .sender(mockReportedUser)
                        .receiver(mockReporter)
                        .date(LocalDateTime.now())
                        .build()
        );

        when(reportService.findById(reportId)).thenReturn(mockReport);
        when(reportService.getChatLogsBetweenUsers(3L, 6L)).thenReturn(mockChatLogs);

        String viewName = adminController.viewChatLogs(reportId, model);

        verify(model).addAttribute("report", mockReport);
        verify(model).addAttribute("chatLogs", mockChatLogs);

        assertEquals("admin/chat-logs", viewName);
    }

    @Test
    public void testResolveReport() {
        Long reportId = 3L;
        boolean banUser = true;
        String description = "User policy violation";
        User mockAdmin = mock(User.class);

        String viewName = adminController.resolveReport(reportId, banUser, description, mockAdmin);

        verify(reportService).resolveReport(reportId, mockAdmin, banUser, description);
        assertEquals("redirect:/admin/dashboard", viewName);
    }

}
