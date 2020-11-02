package io.github.michalczemierowski.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
    private String userName;

    @JsonIgnore
    @ManyToMany(mappedBy = "roomAccess")
    private Set<Room> rooms;

    public User() {
    }

    public User(String id, String userName) {
        this.id = id;
        this.userName = userName;

        rooms = new HashSet<>();
    }

    /**
     * Get list of available rooms
     * (not containing owned rooms)
     *
     * @return list of available rooms
     */
    public Set<Room> getRooms() {
        return rooms;
    }

    /**
     * @return user email
     */
    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
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
     * @param userName new user name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
