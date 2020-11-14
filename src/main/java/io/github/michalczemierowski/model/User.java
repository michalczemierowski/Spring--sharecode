package io.github.michalczemierowski.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @NotBlank
    @Column(length = 40, unique = true, nullable = false)
    private String id;

    @NotBlank
    @Size(min = 5, max = 30)
    @Column(length = 30)
    private String name;

    @JsonBackReference
    @ManyToMany(mappedBy = "usersWithAccess")
    private Set<Room> availableRooms;

    public User() {
    }

    public User(String id, String name) {
        this.id = id;
        this.name = name;

        availableRooms = new HashSet<>();
    }

    /**
     * Get list of available rooms
     * (not containing owned rooms)
     *
     * @return list of available rooms
     */
    public Set<Room> getAvailableRooms() {
        return availableRooms;
    }

    /**
     * @return user email
     */
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /**
     * Update user email
     *
     * @param id new email
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Update user name
     *
     * @param name new user name
     */
    public void setName(String name) {
        this.name = name;
    }
}
