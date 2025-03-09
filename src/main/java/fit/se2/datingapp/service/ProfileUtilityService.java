package fit.se2.datingapp.service;

import fit.se2.datingapp.model.User;
import fit.se2.datingapp.model.UserProfile;
import fit.se2.datingapp.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileUtilityService {
    private final ProfileRepository profileRepository;
    @Autowired
    public ProfileUtilityService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }
    public boolean isProfileExist(User user) {
        return profileRepository.findByUser(user) != null;
    }
    public void createProfile(UserProfile userProfile) {
        profileRepository.save(userProfile);
    }
}
