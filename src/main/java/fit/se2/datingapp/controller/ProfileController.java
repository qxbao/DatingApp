package fit.se2.datingapp.controller;

import fit.se2.datingapp.model.UserProfile;
import fit.se2.datingapp.repository.ProfileRepository;
import fit.se2.datingapp.service.ProfileUtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/profile/")
public class ProfileController {
    @Autowired
    private ProfileUtilityService profileService;
    @Autowired
    private ProfileRepository profileRepository;
    // Todo: Create /profile/init page
    @GetMapping(value = "/init")
    public String createProfile(Model model) {
        UserProfile profile = new UserProfile();
        model.addAttribute("profile", profile);
        return "profile/init";
    }
}
