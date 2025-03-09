package fit.se2.datingapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "like")
@Entity
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id", nullable = false)
    private Long id;

    @ManyToOne
    private User liker;
    @ManyToOne
    private User liked;

    @Column(name = "like_date", nullable = false)
    private LocalDateTime likeDate;
}
