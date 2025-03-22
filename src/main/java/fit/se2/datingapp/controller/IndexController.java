package fit.se2.datingapp.controller;

import fit.se2.datingapp.model.User;
import fit.se2.datingapp.repository.UserRepository;
import fit.se2.datingapp.service.ProfileService;
import fit.se2.datingapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    private final ProfileService profileService;
    @Autowired
    public IndexController(UserService userService, ProfileService profileService) {
        this.profileService = profileService;
    }
    @GetMapping("/")
    public String index(Model model) {
        User user = UserService.getCurrentUser();
        if (user != null) {
            if (user.getRole().equals("BANNED")) {
                return "redirect:/banned";
            }
            if (profileService.isProfileExist(user)) {
                return "app";
            }
            return "redirect:/profile/init";
        } else {
            user = new User();
            model.addAttribute("new_user", user);
            return "index";
        }
    }
    @GetMapping("/banned")
    public String bannedPage(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("BANNED"))) {
            return "redirect:/";
        }
        return "banned";
    }
}
