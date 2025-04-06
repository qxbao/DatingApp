package fit.se2.datingapp.security;

import fit.se2.datingapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private final UserService userService;
    private final ReloadUserAuthentication reloadUserAuthentication;
    @Autowired
    public WebSecurityConfig(UserService userService) {
        this.userService = userService;
        this.reloadUserAuthentication = new ReloadUserAuthentication(userService);
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers("/", "/user/login", "/user/register", "/css/**", "/js/**", "/banned").permitAll()
                    .requestMatchers("/admin/**").hasAuthority("ADMIN")
                    .anyRequest().hasAnyAuthority("USER", "ADMIN")
            )
            .formLogin(form -> form
                .loginPage("/")
                .loginProcessingUrl("/user/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/?error=true")
                .permitAll()
            )
            .rememberMe(Customizer.withDefaults())
            .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .permitAll()
            )
            .csrf(AbstractHttpConfigurer::disable)
            .addFilterBefore(reloadUserAuthentication, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}