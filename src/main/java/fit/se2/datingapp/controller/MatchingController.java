package fit.se2.datingapp.controller;

import fit.se2.datingapp.constants.Const;
import fit.se2.datingapp.dto.ActiveConversationDTO;
import fit.se2.datingapp.dto.ProfileDTO;
import fit.se2.datingapp.dto.SwipeRequestDTO;
import fit.se2.datingapp.dto.SwipeResponseDTO;
import fit.se2.datingapp.model.User;
import fit.se2.datingapp.model.UserMatch;
import fit.se2.datingapp.model.UserPhoto;
import fit.se2.datingapp.model.UserProfile;
import fit.se2.datingapp.service.MatchingService;
import fit.se2.datingapp.service.ProfileUtilityService;
import fit.se2.datingapp.service.UserPhotoUtilityService;
import fit.se2.datingapp.service.UserUtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value="/api/")
public class MatchingController {

    @Autowired
    private ProfileUtilityService profileService;

    @Autowired
    private UserPhotoUtilityService photoService;

    @Autowired
    private MatchingService matchingService;
    @Autowired
    private UserUtilityService userService;

    @PostMapping("/swipe")
    public ResponseEntity<SwipeResponseDTO> handleSwipe(@RequestBody SwipeRequestDTO swipeRequest) {
        User currentUser = UserUtilityService.getCurrentUser();
        assert currentUser != null;
        if (Objects.equals(currentUser.getId(), swipeRequest.getTargetUserId())) {
            return ResponseEntity.badRequest().body(SwipeResponseDTO.builder()
                    .match(false)
                    .message("You cannot swipe on your own profile.")
                    .build());
        }
        // Record the swipe action
        boolean isMatch = matchingService.processSwipe(
                currentUser.getId(),
                swipeRequest.getTargetUserId(),
                swipeRequest.isLike()
        );

        return ResponseEntity.ok(SwipeResponseDTO.builder()
                .match(isMatch)
                .message(isMatch ? "You have a new match!" : "Swipe recorded")
                .build());
    }

    @GetMapping("/next-profile")
    public ResponseEntity<ProfileDTO> getNextProfile() {
        User currentUser = UserUtilityService.getCurrentUser();
        assert currentUser != null;
        UserProfile nextProfile = matchingService.findNextProfileForUser(currentUser);

        if (nextProfile == null) {
            return ResponseEntity.noContent().build();
        }
        System.out.println(nextProfile.getHeight());

        User profileUser = nextProfile.getUser();
        List<UserPhoto> photos = photoService.getUserPhotos(profileUser);
        String mainPhotoUrl = photos.stream()
                .filter(UserPhoto::isProfilePicture)
                .map(UserPhoto::getPhotoUrl)
                .findFirst()
                .orElse(photos.isEmpty() ? null : photos.getFirst().getPhotoUrl());

        ProfileDTO profileDTO = ProfileDTO.builder()
                .id(profileUser.getId())
                .name(profileUser.getName())
                .age(profileService.getAge(profileUser.getDob()))
                .mainPhotoUrl(mainPhotoUrl)
                .photoUrls(photos.stream().map(UserPhoto::getPhotoUrl).collect(Collectors.toList()))
                .bio(nextProfile.getBio())
                .education(nextProfile.getEducation())
                .occupation(nextProfile.getOccupation())
                .height(nextProfile.getHeight())
                .religion(nextProfile.getReligion())
                .build();

        return ResponseEntity.ok(profileDTO);
    }
    @GetMapping("/active-convers")
    public ResponseEntity<ActiveConversationDTO> getActiveConversations() {
        User currentUser = UserUtilityService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.badRequest().body(null);
        } else {
            List<Long> matchIds = matchingService.getMatches(currentUser).stream().map(
                    match -> Objects.equals(match.getUser1().getId(), currentUser.getId()) ? match.getUser2().getId() : match.getUser1().getId()
            ).toList();
            List<String> names = new ArrayList<>();
            List<String> avatarUrls = new ArrayList<>();
            List<String> lastMsgs = new ArrayList<>();
            for (Long uid : matchIds) {
                User user = userService.getUserById(uid);
                if (user == null) return ResponseEntity.badRequest().body(null);
                // Add name
                names.add(user.getName());
                UserPhoto userPhoto = photoService.getUserAvatar(user);
                // Add avatar
                if (userPhoto != null) avatarUrls.add(userPhoto.getPhotoUrl());
                else avatarUrls.add(Const.DEFAULT_AVATAR_URL);
                // Add last message
                lastMsgs.add("Click to enter chat");
            }
            return ResponseEntity.ok(ActiveConversationDTO.builder()
                    .len(matchIds.size())
                    .names(names)
                    .ids(matchIds)
                    .avatarUrls(avatarUrls)
                    .lastMessages(lastMsgs)
                    .build());
        }
    }

}