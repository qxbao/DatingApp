package fit.se2.datingapp.controller;

import fit.se2.datingapp.model.User;
import fit.se2.datingapp.repository.UserRepository;
import fit.se2.datingapp.service.UserUtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/user/")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserUtilityService userService;

    @PostMapping(value="/register")
    public String register(@Validated @ModelAttribute("new_user") User user) {
        user.setRole("USER");
        userService.create(user);
        // Todo: Validate new user info
        return "redirect:/?fromRegister=true";
    }
}
