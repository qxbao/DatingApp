package fit.se2.datingapp.controller;

import fit.se2.datingapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    private final UserRepository userRepository;
    @Autowired
    public IndexController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @GetMapping("/")
    public String index() {
        return "index";
    }
}
