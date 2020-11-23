package io.github.michalczemierowski.service;

import io.github.michalczemierowski.model.User;
import io.github.michalczemierowski.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * @return true if user was added
     */
    public boolean addUserToDatabaseIfNotExists()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User principal = (OAuth2User)auth.getPrincipal();

        String authUserID = principal.getAttribute("email");
        if(userRepository.existsUserById(authUserID))
            return false;

        String name = principal.getAttribute("name");
        User newUser = new User(authUserID, name);

        userRepository.save(newUser);
        return true;
    }

    public Optional<User> getUserById(String userId)
    {
        return userRepository.findById(userId);
    }
}
