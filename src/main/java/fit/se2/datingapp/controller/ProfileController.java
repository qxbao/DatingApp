package fit.se2.datingapp.controller;

import fit.se2.datingapp.dto.AddPhotoDTO;
import fit.se2.datingapp.dto.RemovePhotoDTO;
import fit.se2.datingapp.model.User;
import fit.se2.datingapp.model.UserPhoto;
import fit.se2.datingapp.model.UserProfile;
import fit.se2.datingapp.repository.ProfileRepository;
import fit.se2.datingapp.service.MatchingService;
import fit.se2.datingapp.service.ProfileService;
import fit.se2.datingapp.service.UserPhotoService;
import fit.se2.datingapp.service.UserService;
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
    private final ProfileService profileService;
    private final ProfileRepository profileRepository;
    private final UserPhotoService userPhotoService;
    private final MatchingService matchingService;
    @Autowired
    public ProfileController(
            ProfileService profileService,
            ProfileRepository profileRepository,
            UserPhotoService userPhotoService,
            MatchingService matchingService) {
        this.profileService = profileService;
        this.profileRepository = profileRepository;
        this.userPhotoService = userPhotoService;
        this.matchingService = matchingService;
    }

    @GetMapping(value = "/init")
    public String createProfilePage(Model model) {
        User user = UserService.getCurrentUser();
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
        User user = UserService.getCurrentUser();
        if (profileService.isProfileExist(user)) {
            UserProfile profile = profileRepository.findByUser(user);
            List<UserPhoto> photos = userPhotoService.getUserPhotos(user);
            model.addAttribute("photos", photos);
            model.addAttribute("profile", profile);
            return "profile/update";
        }
        return "redirect:/profile/init";
    }
    @PostMapping(value = "/create")
    public String createProfile(UserProfile profile) {
        User user = UserService.getCurrentUser();
        profile.setUser(user);
        profileService.create(profile);
        return "redirect:/profile/update";
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
                .user(UserService.getCurrentUser())
                .build();
        userPhotoService.create(photo);
        return ResponseEntity.ok(HttpStatus.OK);
    }
    @PostMapping(value = "/photo/remove")
    public ResponseEntity<HttpStatus> removePhoto(@RequestBody RemovePhotoDTO removePhotoDTO) {
        userPhotoService.removePhotoById(removePhotoDTO.getId());
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
