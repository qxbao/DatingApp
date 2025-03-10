package fit.se2.datingapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "user_photo")
@Entity
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class UserPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id", nullable = false)
    private Long id;

    @ManyToOne
    private User user;

    @Column(name = "photo_url", nullable = false)
    private String photoUrl;
    @Column(name = "photo_is_profile_picture", nullable = false)
    private boolean isProfilePicture;
    @Column(name = "photo_upload_time", nullable = false)
    private LocalDateTime uploadDate;
}
