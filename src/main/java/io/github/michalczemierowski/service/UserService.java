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
     * [ READS USER DATA FROM AUTH INFO ]
     * @return true if user was added
     */
    public boolean addUserToDatabaseIfNotExists() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User principal = (OAuth2User) auth.getPrincipal();

        String authUserID = principal.getAttribute("email");
        if (userRepository.existsUserById(authUserID))
            return false;

        String name = principal.getAttribute("name");
        User newUser = new User(authUserID, name);

        userRepository.save(newUser);
        return true;
    }

    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }

    public Optional<String> getNameById(String userId) {
        Optional<User> optionalUser = userRepository.findById(userId);

        return optionalUser.isPresent()
                ? Optional.of(optionalUser.get().getName())
                : Optional.empty();
    }

    public boolean setUserName(String userId, String name) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // TODO: name validations
            user.setName(name);
            userRepository.save(user);
            return true;
        }

        return false;
    }
}
