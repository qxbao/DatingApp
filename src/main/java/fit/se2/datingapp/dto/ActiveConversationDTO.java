package fit.se2.datingapp.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ActiveConversationDTO {
    private int len;
    private List<String> names;
    private List<String> avatarUrls;
    private List<String> lastMessages;
}