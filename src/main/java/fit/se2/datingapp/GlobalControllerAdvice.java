package fit.se2.datingapp;

import fit.se2.datingapp.constants.Const;
import fit.se2.datingapp.repository.*;
import fit.se2.datingapp.model.*;
import fit.se2.datingapp.service.MatchingService;
import fit.se2.datingapp.service.UserPhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Objects;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchingService matchingService;

    @Autowired
    private UserPhotoService userPhotoService;

    @ModelAttribute("user")
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() &&
                !(auth.getPrincipal() instanceof String && auth.getPrincipal().equals("anonymousUser"))) {

            if (auth.getPrincipal() instanceof User user) {
                return userRepository.findById(user.getId()).orElse(user);
            } else if (auth.getPrincipal() instanceof UserDetails userDetails) {
                return userRepository.findByEmail(userDetails.getUsername());
            }
        }

        return null;
    }

    @ModelAttribute("avatar")
    public String getAvatarUrl(@ModelAttribute("user") User user) {
        if (user != null) {
            UserPhoto userPhoto = userPhotoService.getUserAvatar(user);
            return userPhoto != null ? userPhoto.getPhotoUrl() : Const.DEFAULT_AVATAR_URL;
        }
        return null;
    }

    @ModelAttribute("likerNo")
    public int getLikersCount(@ModelAttribute("user") User user) {
        if (user != null) {
            return matchingService.getNumberOfALikeB(user);
        }
        return 0;
    }

    @ModelAttribute("match_users")
    public List<User> getMatches(@ModelAttribute("user") User user) {
        if (user != null) {
            List<UserMatch> matches = matchingService.getMatches(user);
            return matches.stream()
                    .map(match -> Objects.equals(match.getUser1().getId(), user.getId()) ? match.getUser2() : match.getUser1())
                    .toList();
        }
        return List.of();
    }
    @ModelAttribute("match_photos")
    public List<UserPhoto> getPhotos(@ModelAttribute("user") User user) {
        if (user != null) {
            List<UserMatch> matches = matchingService.getMatches(user);
            List<User> matchedUsers = matches.stream()
                    .map(match -> Objects.equals(match.getUser1().getId(), user.getId()) ? match.getUser2() : match.getUser1())
                    .toList();
            return matchedUsers.stream()
                    .map(u -> userPhotoService.getUserPhotos(u))
                    .flatMap(List::stream)
                    .toList();
        }
        return List.of();
    }
}