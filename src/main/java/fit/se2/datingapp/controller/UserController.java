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
    private final UserRepository userRepository;
    private final UserUtilityService userService;
    @Autowired
    public UserController(UserRepository userRepository, UserUtilityService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostMapping(value="/register")
    public String register(@Validated @ModelAttribute("new_user") User user) {
        user.setRole("USER");
        // Todo: Validate new user info
        userService.create(user);
        return "redirect:/?fromRegister=true";
    }
}
