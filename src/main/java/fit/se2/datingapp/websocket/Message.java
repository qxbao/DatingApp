package fit.se2.datingapp.websocket;

import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class Message {
    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
    private MessageType type;
    private Long sender;
    private Long receiver;
    private String content;
}
