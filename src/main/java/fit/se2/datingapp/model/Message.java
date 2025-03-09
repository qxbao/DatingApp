package fit.se2.datingapp.model;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "message")
@Entity
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Message {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id", nullable = false)
    private Long id;

    @ManyToOne
    private User sender;
    @ManyToOne
    private User receiver;

    @Column(name = "message_content", nullable = false)
    private String content;
    @Column(name = "message_date", nullable = false)
    private LocalDateTime date;
}
