package fit.se2.datingapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id", nullable = false)
    private Long id;
    @Column(name="user_name", nullable = false)
    private String name;
    @Column(name="user_age", nullable = false)
    private int age;
    @Column(name="user_email", nullable = false, unique = true)
    private String email;
    @Column(name="user_gender", nullable = false)
    private String gender;
    @Column(name="user_pref", nullable = false)
    private String preference;
    @Column(name="user_password", nullable = false)
    private String password;
}
