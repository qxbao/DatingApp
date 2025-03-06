package fit.se2.datingapp.controller;

import fit.se2.datingapp.model.User;
import fit.se2.datingapp.repository.UserRepository;
import fit.se2.datingapp.service.UserUtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    private final UserRepository userRepository;
    @Autowired
    public IndexController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @GetMapping("/")
    public String index(Model model) {
        if (UserUtilityService.isAuth()) {
            return "dashboard";
        } else {
            User user = new User();
            model.addAttribute("new_user", user);
            return "index";
        }

    }
}
