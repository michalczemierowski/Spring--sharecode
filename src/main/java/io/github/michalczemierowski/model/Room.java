package io.github.michalczemierowski.model;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @Type(type = "uuid-char")
    @Column(length = 36, unique = true, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    private User ownerUser;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "room_access",
            joinColumns = @JoinColumn(name = "room_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_email", referencedColumnName = "id"))
    private Set<User> roomAccess;

    @Column(columnDefinition = "TEXT")
    private String content;

    // region Constructors

    public Room() {
    }

    public Room(UUID id, String name) {
        this.id = id;
        this.name = name;

        this.roomAccess = new HashSet<>();
    }

    public Room(UUID id, String name, User ownerUser, User... roomAccess) {
        this.id = id;
        this.name = name;
        this.ownerUser = ownerUser;

        this.roomAccess = Stream.of(roomAccess).collect(Collectors.toSet());
        this.roomAccess.forEach(x -> x.getRooms().add(this));
    }

    // endregion

    // region Getters

    /**
     * Get room's owner
     *
     * @return room's owner
     */
    public User getOwnerUser() {
        return ownerUser;
    }

    /**
     * Get set of users with access to this room
     *
     * @return set of users with access to this room
     */
    public Set<User> getRoomAccess() {
        return roomAccess;
    }

    /**
     * Get room name
     *
     * @return room name
     */
    public String getName() {
        return name;
    }

    /**
     * Get room id
     *
     * @return room id
     */
    public UUID getId() {
        return id;
    }

    /**
     * Get room content
     *
     * @return room content
     */
    public String getContent() {
        return content;
    }

    // endregion

    // region Setters

    /**
     * Update room owner and gain access to this room for current owner
     *
     * @param user new owner
     */
    public void setOwnerUser(User user) {
        if (user == null || this.ownerUser.equals(user))
            return;

        roomAccess.remove(user);

        roomAccess.add(ownerUser);
        ownerUser = user;
    }

    /**
     * Update room name
     *
     * @param name new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Add access to this room for user
     *
     * @param user user that will gain access to this room
     * @return true if success
     */
    public boolean addUser(User user) {
        if (user.equals(ownerUser))
            return false;

        if (roomAccess.contains(user))
            return false;

        roomAccess.add(user);
        user.getRooms().add(this);
        return true;
    }

    /**
     * Remove access to this room for user
     *
     * @param user user that will lose access to this room
     */
    public void removeUser(User user) {
        if (roomAccess == null)
            return;

        roomAccess.remove(user);
    }

    /**
     * Set room content
     *
     * @param content new content
     */
    public void setContent(String content) {
        this.content = content;
    }
}
