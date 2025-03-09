package fit.se2.datingapp.controller;

import fit.se2.datingapp.model.User;
import fit.se2.datingapp.repository.UserRepository;
import fit.se2.datingapp.service.ProfileUtilityService;
import fit.se2.datingapp.service.UserUtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    private final UserRepository userRepository;
    private final UserUtilityService userService;
    private final ProfileUtilityService profileService;
    @Autowired
    public IndexController(UserRepository userRepository, UserUtilityService userService, ProfileUtilityService profileService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.profileService = profileService;
    }
    @GetMapping("/")
    public String index(Model model) {
        User user = UserUtilityService.getCurrentUser();
        if (user != null) {
            if (profileService.isProfileExist(user)) {
                return "dashboard";
            }
            return "redirect:/profile/init";
        } else {
            user = new User();
            model.addAttribute("new_user", user);
            return "index";
        }

    }
}
