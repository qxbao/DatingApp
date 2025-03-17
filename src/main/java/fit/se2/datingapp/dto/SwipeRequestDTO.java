package fit.se2.datingapp.dto;

import lombok.Data;

@Data
public class SwipeRequestDTO {
    private Long targetUserId;
    private boolean like;
}