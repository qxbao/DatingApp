package fit.se2.datingapp.dto;

import lombok.Data;

@Data
public class UserReportDTO {
    private Long reportedUserId;
    private String reason;
}
