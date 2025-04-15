package fit.se2.datingapp.service;

import fit.se2.datingapp.model.User;
import fit.se2.datingapp.model.UserPhoto;
import fit.se2.datingapp.repository.UserPhotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserPhotoServiceTest {

    @Mock
    private UserPhotoRepository userPhotoRepository;

    @InjectMocks
    private UserPhotoService userPhotoService;

    private User user;
    private UserPhoto photo1;
    private UserPhoto photo2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .id(1L)
                .name("Nam Vu")
                .dob(LocalDate.of(2004, 4, 12))
                .email("namvu369@gmail.com")
                .role("USER")
                .gender("MALE")
                .preference("FEMALE")
                .password("12345678")
                .build();

        photo1 = UserPhoto.builder()
                .id(1L)
                .user(user)
                .photoUrl("https://photo.com/photo1.png")
                .isProfilePicture(false)
                .uploadDate(LocalDateTime.now())
                .build();

        photo2 = UserPhoto.builder()
                .id(2L)
                .user(user)
                .photoUrl("https://photo.com/photo2.png")
                .isProfilePicture(true)
                .uploadDate(LocalDateTime.now())
                .build();
    }

    @Test
    public void testGetUserPhotos_ReturnsPhotos() {
        when(userPhotoRepository.findByUser(user)).thenReturn(Optional.of(List.of(photo1, photo2)));

        List<UserPhoto> result = userPhotoService.getUserPhotos(user);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userPhotoRepository).findByUser(user);
    }

    @Test
    public void testGetUserPhotos_ReturnsNullIfNoPhotos() {
        when(userPhotoRepository.findByUser(user)).thenReturn(Optional.empty());

        List<UserPhoto> result = userPhotoService.getUserPhotos(user);

        assertNull(result);
        verify(userPhotoRepository).findByUser(user);
    }

    @Test
    public void testGetUserAvatar_ReturnsProfilePicture() {
        when(userPhotoRepository.findByUser(user)).thenReturn(Optional.of(List.of(photo1, photo2)));

        UserPhoto avatar = userPhotoService.getUserAvatar(user);

        assertNotNull(avatar);
        assertTrue(avatar.isProfilePicture());
        assertEquals(2L, avatar.getId());
    }

    @Test
    public void testGetUserAvatar_ReturnsFirstIfNoProfilePicture() {
        photo1.setProfilePicture(false);
        when(userPhotoRepository.findByUser(user)).thenReturn(Optional.of(List.of(photo1)));

        UserPhoto avatar = userPhotoService.getUserAvatar(user);

        assertNotNull(avatar);
        assertEquals(photo1.getId(), avatar.getId());
    }

    @Test
    public void testGetUserAvatar_ReturnsNullIfNoPhotos() {
        when(userPhotoRepository.findByUser(user)).thenReturn(Optional.empty());

        UserPhoto avatar = userPhotoService.getUserAvatar(user);

        assertNull(avatar);
    }

    @Test
    public void testCreate_CallsSave() {
        userPhotoService.create(photo1);

        verify(userPhotoRepository).save(photo1);
    }

    @Test
    public void testRemovePhotoById_CallsDeleteById() {
        userPhotoService.removePhotoById(1L);

        verify(userPhotoRepository).deleteById(1L);
    }
}
