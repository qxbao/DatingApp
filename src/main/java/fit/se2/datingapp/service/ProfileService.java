package fit.se2.datingapp.service;

import fit.se2.datingapp.model.User;
import fit.se2.datingapp.model.UserProfile;
import fit.se2.datingapp.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;
    @Autowired
    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }
    public boolean isProfileExist(User user) {
        return profileRepository.findByUser(user) != null;
    }
    public void create(UserProfile userProfile) {
        profileRepository.save(userProfile);
    }
    public int getAge(LocalDate birthDate) {
        LocalDate today = LocalDate.now();
        int age = today.getYear() - birthDate.getYear();
        if (today.getDayOfYear() < birthDate.getDayOfYear()) {
            age--;
        }
        return age;
    }
}
