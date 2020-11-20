package io.github.michalczemierowski.service;

import io.github.michalczemierowski.model.User;
import io.github.michalczemierowski.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> getUserById(String userId)
    {
        return userRepository.findById(userId);
    }
}
