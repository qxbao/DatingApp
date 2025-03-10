package fit.se2.datingapp.controller;

import fit.se2.datingapp.model.User;
import fit.se2.datingapp.model.UserProfile;
import fit.se2.datingapp.repository.ProfileRepository;
import fit.se2.datingapp.service.ProfileUtilityService;
import fit.se2.datingapp.service.UserUtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/profile/")
public class ProfileController {
    @Autowired
    private ProfileUtilityService profileService;
    @Autowired
    private ProfileRepository profileRepository;
    @GetMapping(value = "/init")
    public String createProfilePage(Model model) {
        User user = UserUtilityService.getCurrentUser();
        if (profileService.isProfileExist(user)) {
            return "redirect:/";
        }
        UserProfile profile = new UserProfile();
        model.addAttribute("profile", profile);
        model.addAttribute("user", user);
        return "profile/init";
    }
    @PostMapping(value = "/create")
    public String createProfile(UserProfile profile) {
        User user = UserUtilityService.getCurrentUser();
        profile.setUser(user);
        profileService.create(profile);
        return "redirect:/";
    }
}
