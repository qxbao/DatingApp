package fit.se2.datingapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "user_swipe")
@Entity
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class UserSwipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "swipe_id", nullable = false)
    private Long id;

    @ManyToOne
    private User liker;
    @ManyToOne
    private User liked;

    @Column(name = "swipe_is_like", nullable = false)
    private boolean isLike;
    @Column(name = "swipe_date", nullable = false)
    private LocalDateTime likeDate;
}
