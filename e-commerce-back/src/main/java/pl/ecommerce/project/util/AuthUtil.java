package pl.ecommerce.project.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import pl.ecommerce.project.model.User;
import pl.ecommerce.project.repo.UserRepository;

@Component
public class AuthUtil {
    private final UserRepository userRepository;

    public AuthUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String loggedInEmail() {
        User user = getUser();
        return user.getEmail();
    }

    public Long loggedInUserId() {
        User user = getUser();

        return user.getUserId();
    }

    public User loggedInUser() {
        return getUser();
    }

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return userRepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username" + authentication.getName()));
    }
}
