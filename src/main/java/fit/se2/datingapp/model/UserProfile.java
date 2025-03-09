package fit.se2.datingapp.model;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "profile")
@Entity
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id", nullable = false)
    private Long id;

    @OneToOne
    private User user;

    @Column(name = "profile_height")
    private Integer height;
    @Column(name = "profile_occupation")
    private String occupation;
    @Column(name = "profile_education")
    private String education;
    @Column(name="profile_bio")
    private String bio;
    @Column(name="profile_religion")
    private String religion;
}