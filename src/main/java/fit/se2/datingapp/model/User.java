package fit.se2.datingapp.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Table(name = "user")
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    private static final String AUTHORITIES_DELIMITER = "::";

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
    @Column(name="user_role", nullable = false)
    private String role;
    @Column(name="user_gender", nullable = false)
    private String gender;
    @Column(name="user_pref", nullable = false)
    private String preference;
    @Column(name="user_password", nullable = false)
    private String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(this.role.split(AUTHORITIES_DELIMITER))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
