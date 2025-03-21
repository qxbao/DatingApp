package fit.se2.datingapp.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Table(name = "profile")
@Entity
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id", nullable = false)
    private Long id;

    @OneToOne(optional = false)
    private User user;

    @Column(name = "profile_height")
    private String height;
    @Column(name = "profile_occupation", length = 20)
    private String occupation;
    @Column(name = "profile_education", length = 20)
    private String education;
    @Column(name="profile_bio", length = 200)
    private String bio;
    @Column(name="profile_religion")
    private String religion;
}