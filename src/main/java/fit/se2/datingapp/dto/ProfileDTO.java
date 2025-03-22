package fit.se2.datingapp.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ProfileDTO {
    private Long id;
    private String name;
    private int age;
    private List<String> photoUrls;
    private String bio;
    private String occupation;
    private String education;
    private String height;
    private String religion;
}