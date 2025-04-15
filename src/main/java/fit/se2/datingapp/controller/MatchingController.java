package fit.se2.datingapp.controller;

import fit.se2.datingapp.constants.Const;
import fit.se2.datingapp.dto.*;
import fit.se2.datingapp.model.User;
import fit.se2.datingapp.model.UserPhoto;
import fit.se2.datingapp.model.UserProfile;
import fit.se2.datingapp.service.MatchingService;
import fit.se2.datingapp.service.ProfileService;
import fit.se2.datingapp.service.UserPhotoService;
import fit.se2.datingapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value="/api/")
public class MatchingController {

    private final ProfileService profileService;
    private final UserPhotoService photoService;
    private final MatchingService matchingService;
    private final UserService userService;
    @Autowired
    public MatchingController(
            ProfileService profileService,
            UserPhotoService photoService,
            MatchingService matchingService,
            UserService userService) {
        this.profileService = profileService;
        this.photoService = photoService;
        this.matchingService = matchingService;
        this.userService = userService;
    }

    @PostMapping("/swipe")
    public ResponseEntity<SwipeResponseDTO> handleSwipe(@RequestBody SwipeRequestDTO swipeRequest) {
        User currentUser = UserService.getCurrentUser();
        assert currentUser != null;
        if (Objects.equals(currentUser.getId(), swipeRequest.getTargetUserId())) {
            return ResponseEntity.badRequest().body(SwipeResponseDTO.builder()
                    .match(false)
                    .message("You cannot swipe on your own profile.")
                    .build());
        }

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
        User currentUser = UserService.getCurrentUser();
        assert currentUser != null;
        UserProfile nextProfile = matchingService.findNextProfileForUser(currentUser);

        if (nextProfile == null) {
            return ResponseEntity.noContent().build();
        }

        User profileUser = nextProfile.getUser();
        List<UserPhoto> photos = photoService.getUserPhotos(profileUser);

        ProfileDTO profileDTO = ProfileDTO.builder()
                .id(profileUser.getId())
                .name(profileUser.getName())
                .age(profileService.getAge(profileUser.getDob()))
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
        User currentUser = UserService.getCurrentUser();
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

    @PostMapping(value = "/unmatch")
    public ResponseEntity<String> unmatch(
        @ModelAttribute("user") User user,
        @RequestBody UnmatchRequestDTO request
    ) {
        try {
            matchingService.unmatch(user, userService.getUserById(request.getTargetId()));
        } catch (Exception e) {
            ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
        return ResponseEntity.ok("Ok");
    }
    
    @PostMapping(value = "/report")
    public ResponseEntity<String> reportUser(
        @RequestBody UserReportDTO request,
        @ModelAttribute("user") User user
    ) {
        if (!matchingService.isAMatchBetween(user, userService.getUserById(request.getReportedUserId()))) {
            return ResponseEntity.badRequest().body("Error: You can only report users you have matched with.");
        }
        try {
            matchingService.reportUser(
                    user,
                    userService.getUserById(request.getReportedUserId()),
                    request.getReason());
            UnmatchRequestDTO unmatchRequest = new UnmatchRequestDTO();
            unmatchRequest.setTargetId(request.getReportedUserId());
            unmatch(user, unmatchRequest);
        } catch (Exception e) {
            ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
        return ResponseEntity.ok("Ok");
    }
}