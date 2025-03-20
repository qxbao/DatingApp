package fit.se2.datingapp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendMessageDTO {
    private Long senderId;
    private Long receiverId;
    private String content;
}
