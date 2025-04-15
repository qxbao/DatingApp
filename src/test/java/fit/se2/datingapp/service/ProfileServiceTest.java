package fit.se2.datingapp.service;

import fit.se2.datingapp.model.User;
import fit.se2.datingapp.model.UserProfile;
import fit.se2.datingapp.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ProfileService profileService;

    private User user;
    private UserProfile profile;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder().id(1L).name("Nam Vu").build();
        profile = UserProfile.builder().id(1L).user(user).bio("Hi there!").build();
    }

    @Test
    public void testIsProfileExist_ReturnsTrue() {
        when(profileRepository.findByUser(user)).thenReturn(profile);

        boolean exists = profileService.isProfileExist(user);

        assertTrue(exists);
        verify(profileRepository).findByUser(user);
    }

    @Test
    public void testIsProfileExist_ReturnsFalse() {
        when(profileRepository.findByUser(user)).thenReturn(null);

        boolean exists = profileService.isProfileExist(user);

        assertFalse(exists);
        verify(profileRepository).findByUser(user);
    }

    @Test
    public void testCreate_CallsRepositorySave() {
        profileService.create(profile);

        verify(profileRepository).save(profile);
    }

    @Test
    public void testGetAge_ExactBirthday() {
        LocalDate today = LocalDate.now();
        LocalDate birthDate = today.minusYears(25);

        int age = profileService.getAge(birthDate);

        assertEquals(25, age);
    }


    @Test
    public void testGetAge_BirthdayYetToComeThisYear() {
        LocalDate today = LocalDate.now();
        LocalDate birthDate = today.minusYears(25).plusDays(1);

        int age = profileService.getAge(birthDate);

        assertEquals(24, age);
    }

    @Test
    public void testFindByUser() {
        when(profileRepository.findByUser(user)).thenReturn(profile);

        UserProfile result = profileService.findByUser(user);

        assertNotNull(result);
        assertEquals("Hi there!", result.getBio());
        verify(profileRepository).findByUser(user);
    }
}
