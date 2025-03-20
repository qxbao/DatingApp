package fit.se2.datingapp.dto;

import lombok.Data;

@Data
public class CreateAccountRequestDTO {
    private String name;
    private String email;
    private String gender;
    private String preference;
    private String dob;
    private String password;
}
