package io.github.michalczemierowski.repository;

import io.github.michalczemierowski.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsUserById(String id);
}
