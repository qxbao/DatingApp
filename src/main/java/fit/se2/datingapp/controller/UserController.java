package fit.se2.datingapp.controller;

import fit.se2.datingapp.dto.CreateAccountRequestDTO;
import fit.se2.datingapp.dto.CreateAccountResponseDTO;
import fit.se2.datingapp.model.User;
import fit.se2.datingapp.service.ProfileService;
import fit.se2.datingapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping(value = "/user/")
public class UserController {
    private final UserService userService;
    private final ProfileService profileService;

    @Autowired
    public UserController(UserService userService, ProfileService profileService) {
        this.userService = userService;
        this.profileService = profileService;
    }

    @PostMapping(value="/register")
    public ResponseEntity<CreateAccountResponseDTO> register(@RequestBody CreateAccountRequestDTO requestDTO) {
        String message = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (requestDTO.getName() == null || requestDTO.getEmail() == null || requestDTO.getDob() == null || requestDTO.getGender() == null || requestDTO.getPreference() == null || requestDTO.getPassword() == null) {
            message = "All fields are required";
        } else if (requestDTO.getName().length() < 3) message = "Name must be at least 3 characters long";
        else if (requestDTO.getPassword().length() < 8) message = "Password must be at least 8 characters long";
        else if (profileService.getAge(
                LocalDate.parse(requestDTO.getDob(), formatter)) < 18) message = "User must be at least 18 years old";
        else if (userService.isEmailExist(requestDTO.getEmail())) message = "Email already exists";
        if (message != null) {
            return ResponseEntity.ok().body(
                CreateAccountResponseDTO.builder()
                    .isError(true)
                    .errorMessage(message)
                    .build()
            );
        }
        try {
            new User();
            User user = User.builder()
                    .name(requestDTO.getName())
                    .email(requestDTO.getEmail())
                    .dob(LocalDate.parse(requestDTO.getDob(), formatter))
                    .gender(requestDTO.getGender())
                    .preference(requestDTO.getPreference())
                    .password(requestDTO.getPassword())
                    .role("USER")
                    .build();
            userService.create(user);
            return ResponseEntity.ok().body(
                CreateAccountResponseDTO.builder()
                    .isError(false)
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.ok().body(
                CreateAccountResponseDTO.builder()
                    .isError(true)
                    .errorMessage("Error creating account")
                    .build()
            );
        }
    }
}
