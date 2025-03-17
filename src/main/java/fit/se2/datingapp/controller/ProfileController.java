package fit.se2.datingapp.controller;

import fit.se2.datingapp.dto.AddPhotoDTO;
import fit.se2.datingapp.model.User;
import fit.se2.datingapp.model.UserPhoto;
import fit.se2.datingapp.model.UserProfile;
import fit.se2.datingapp.repository.ProfileRepository;
import fit.se2.datingapp.service.ProfileUtilityService;
import fit.se2.datingapp.service.UserPhotoUtilityService;
import fit.se2.datingapp.service.UserUtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping(value = "/profile/")
public class ProfileController {
    @Autowired
    private ProfileUtilityService profileService;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private UserPhotoUtilityService userPhotoUtilityService;

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
    @GetMapping(value = "/update")
    public String updateProfilePage(Model model) {
        User user = UserUtilityService.getCurrentUser();
        if (profileService.isProfileExist(user)) {
            UserProfile profile = profileRepository.findByUser(user);
            List<UserPhoto> photos = userPhotoUtilityService.getUserPhotos(user);
            model.addAttribute("photos", photos);
            model.addAttribute("user", user);
            model.addAttribute("profile", profile);
            return "profile/update";
        }
        return "redirect:/profile/init";
    }
    @PostMapping(value = "/create")
    public String createProfile(UserProfile profile) {
        User user = UserUtilityService.getCurrentUser();
        profile.setUser(user);
        profileService.create(profile);
        return "redirect:/";
    }
    @PostMapping(value = "/update")
    public String updateProfile(UserProfile profile) {
        profileService.create(profile);
        return "redirect:/";
    }
    @PostMapping(value = "/photo/add")
    public ResponseEntity<HttpStatus> addPhoto(@RequestBody AddPhotoDTO addPhotoDTO) {
        if (addPhotoDTO.getUrl() == null || addPhotoDTO.getUrl().isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        UserPhoto photo = UserPhoto.builder()
                .photoUrl(addPhotoDTO.getUrl())
                .uploadDate(LocalDateTime.now())
                .isProfilePicture(true)
                .user(UserUtilityService.getCurrentUser())
                .build();
        userPhotoUtilityService.create(photo);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
