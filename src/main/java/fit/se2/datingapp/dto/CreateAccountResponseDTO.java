package fit.se2.datingapp.dto;

import lombok.Builder;
import lombok.Data;

@Builder @Data
public class CreateAccountResponseDTO {
    private boolean isError;
    private String errorMessage;
}