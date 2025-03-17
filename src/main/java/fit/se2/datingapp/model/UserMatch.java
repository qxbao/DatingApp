package fit.se2.datingapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "user_match")
@Entity
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class UserMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_id", nullable = false)
    private Long id;

    @ManyToOne
    private User user1;
    @ManyToOne
    private User user2;

    @Column(name = "match_date", nullable = false)
    private LocalDateTime matchDate;
}
