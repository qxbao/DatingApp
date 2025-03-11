package fit.se2.datingapp.service;

import fit.se2.datingapp.model.User;
import fit.se2.datingapp.model.UserPhoto;
import fit.se2.datingapp.model.UserProfile;
import fit.se2.datingapp.repository.UserPhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPhotoUtilityService {
    UserPhotoRepository userPhotoRepository;
    @Autowired
    public UserPhotoUtilityService(UserPhotoRepository userPhotoRepository) {
        this.userPhotoRepository = userPhotoRepository;
    }
    public List<UserPhoto> getUserPhotos(User user) {
        return userPhotoRepository.findByUser(user);
    }
    public void create(UserPhoto userPhoto) {
        userPhotoRepository.save(userPhoto);
    }
}
