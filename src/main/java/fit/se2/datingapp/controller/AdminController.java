package fit.se2.datingapp.controller;

import fit.se2.datingapp.model.User;
import fit.se2.datingapp.model.UserReport;
import fit.se2.datingapp.service.ProfileService;
import fit.se2.datingapp.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ReportService reportService;

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(name = "filter", defaultValue = "unsolved") String filter, Model model) {
        switch (filter) {
            case "all":
                model.addAttribute("reports", reportService.findAll());
                break;
            case "resolved":
                model.addAttribute("reports", reportService.findAllResolved());
                break;
            case "unsolved":
            default:
                model.addAttribute("reports", reportService.findAllUnsolved());
                break;
        }
        model.addAttribute("currentFilter", filter);
        return "admin/dashboard";
    }

    @GetMapping("/report/{rid}")
    public String viewReport(@PathVariable Long rid, Model model, @ModelAttribute("user") User user) {
        UserReport report = reportService.findById(rid);
        model.addAttribute("report", report);
        return "admin/report-detail";
    }

    @GetMapping("/report/{rid}/chat")
    public String viewChatLogs(@PathVariable Long rid, Model model) {
        UserReport report = reportService.findById(rid);
        model.addAttribute("report", report);
        model.addAttribute("chatLogs", reportService.getChatLogsBetweenUsers(
                report.getReporter().getId(), report.getReportedUser().getId()));
        return "admin/chat-logs";
    }


    @PostMapping("/report/{rid}/resolve")
    public String resolveReport(
            @PathVariable Long rid,
            @RequestParam boolean banUser,
            @RequestParam String description,
            @AuthenticationPrincipal User admin) {
        reportService.resolveReport(rid, admin, banUser, description);
        return "redirect:/admin/dashboard";
    }
}
