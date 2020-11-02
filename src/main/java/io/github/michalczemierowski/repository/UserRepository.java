package io.github.michalczemierowski.repository;

import io.github.michalczemierowski.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, String> {
}
