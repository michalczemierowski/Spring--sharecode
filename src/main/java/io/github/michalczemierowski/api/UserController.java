package io.github.michalczemierowski.api;

import io.github.michalczemierowski.model.User;
import io.github.michalczemierowski.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RequestMapping("api/v1/user")
@RestController
public class UserController {
    private final DatabaseService dbService;

    @Autowired
    public UserController(DatabaseService dbService) {
        this.dbService = dbService;
    }

    /**
     * TODO: remove later
     */
    @GetMapping("get")
    public List<User> getAllUsers() {
        return dbService.getAllUsers();
    }

    /**
     * Get user by email
     * @param email user email
     * @return user object if exists, else throw 404
     */
    @GetMapping(path = "get/{email}")
    public User getUserByID(@PathVariable("email") String email) {
        Optional<User> user = dbService.findUserByID(email);

        if (user.isPresent())
            return user.get();
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find resource");
    }
}
