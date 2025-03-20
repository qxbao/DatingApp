package fit.se2.datingapp.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetMessageResponseDTO {
    private List<String> messages;
    private List<Long> senders;
    private boolean isOverflow;
}
