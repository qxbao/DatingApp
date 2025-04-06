package fit.se2.datingapp.security;

import fit.se2.datingapp.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ReloadUserAuthentication extends OncePerRequestFilter {
    private final UserService userService;
    @Autowired
    public ReloadUserAuthentication(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetails currentUser) {
            UserDetails freshUser = userService.loadUserByUsername(currentUser.getUsername());
            if (freshUser.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("BANNED"))) {
                SecurityContextHolder.clearContext();
                if (!request.getRequestURI().equals("/banned")) {
                    response.sendRedirect("/banned");
                    return;
                }
            }
            if (!currentUser.getAuthorities().equals(freshUser.getAuthorities())) {
                Authentication newAuth = new UsernamePasswordAuthenticationToken(
                        freshUser, auth.getCredentials(), freshUser.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(newAuth);
            }
        }
        chain.doFilter(request, response);
    }
}