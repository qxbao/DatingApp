package fit.se2.datingapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "user_report")
@Entity @Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id", nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    private User reporter;

    @ManyToOne(optional = false)
    private User reportedUser;

    @Column(name = "report_reason", nullable = false)
    private String reason;

    @Column(name = "report_solved")
    private boolean isSolved;

    @ManyToOne
    private User solver;

    @Column(name = "report_solving_description")
    private String solvingDescription;

    @Column(name = "report_time")
    private LocalDateTime time;

    @Column(name = "report_solved_time")
    private LocalDateTime solvedTime;
}
