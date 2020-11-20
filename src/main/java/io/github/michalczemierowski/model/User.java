package io.github.michalczemierowski.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

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
    private List<Room> availableRooms;

    @JsonIgnore
    @OneToMany(mappedBy = "ownerUser")
    private List<Room> ownedRooms;

    public User() {
    }

    public User(String id, String name) {
        this.id = id;
        this.name = name;

        availableRooms = new ArrayList<>();
    }

    /**
     * @return list of available rooms (not containing owned rooms)
     */
    public List<Room> getAvailableRooms() {
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
     * @return List of owned rooms
     */
    public List<Room> getOwnedRooms() {
        if(ownedRooms == null)
            ownedRooms = new ArrayList<>();

        return ownedRooms;
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

    /**
     * Update owned rooms
     * @param ownedRooms new owned rooms list
     */
    public void setOwnedRooms(List<Room> ownedRooms)
    {
        this.ownedRooms = ownedRooms;
    }
}
