package fit.se2.datingapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "match")
@Entity
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Match {
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
