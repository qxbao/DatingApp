package fit.se2.datingapp.service;

import fit.se2.datingapp.model.User;
import fit.se2.datingapp.model.UserPhoto;
import fit.se2.datingapp.repository.UserPhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPhotoService {
    UserPhotoRepository userPhotoRepository;
    @Autowired
    public UserPhotoService(UserPhotoRepository userPhotoRepository) {
        this.userPhotoRepository = userPhotoRepository;
    }
    public List<UserPhoto> getUserPhotos(User user) {
        return userPhotoRepository.findByUser(user).orElse(null);
    }
    public UserPhoto getUserAvatar(User user) {
        List<UserPhoto> photos = userPhotoRepository.findByUser(user).orElse(null);
        if  (photos != null && !photos.isEmpty()) {
            return photos.stream()
                .filter(UserPhoto::isProfilePicture)
                .findFirst()
                .orElse(photos.getFirst());
        }
        return null;
    }
    public void create(UserPhoto userPhoto) {
        userPhotoRepository.save(userPhoto);
    }

    public void removePhotoById(Long id) {
        userPhotoRepository.deleteById(id);
    }
}
