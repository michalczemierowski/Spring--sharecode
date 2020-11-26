package io.github.michalczemierowski.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id
    @NotBlank
    @Column(length = 40, unique = true, nullable = false)
    private String id;

    @NotBlank
    @Size(min = 5, max = 32)
    @Column(length = 32)
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

    /**
     * @return user name
     */
    public String getName() {
        return name;
    }

    /**
     * @return list of owned rooms
     */
    public List<Room> getOwnedRooms() {
        if (ownedRooms == null)
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
     *
     * @param ownedRooms new owned rooms list
     */
    public void setOwnedRooms(List<Room> ownedRooms) {
        this.ownedRooms = ownedRooms;
    }

    // region Utils

    /**
     * Check if room can be viewed by user
     * @param roomId room id
     * @return true if user can view room
     */
    public boolean canView(UUID roomId) {
        if (isOwnerOf(roomId))
            return true;

        return hasAccessTo(roomId);
    }

    /**
     * Check if room is in available rooms list
     * (don't checks if room is on owned rooms list)
     * @param roomId room id
     * @return true if room is in list
     */
    public boolean hasAccessTo(UUID roomId) {
        return availableRooms.stream().anyMatch(room -> room.getId().equals(roomId));
    }

    /**
     * Check if user is owner of room
     * @param roomId room id
     * @return true if user is owner
     */
    public boolean isOwnerOf(UUID roomId) {
        return ownedRooms.stream().anyMatch(room -> room.getId().equals(roomId));
    }

    // endregion
}
