package io.github.michalczemierowski.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Type;
import org.springframework.boot.context.properties.bind.DefaultValue;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    @Size(min = 5, max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "date_of_last_use", nullable = false)
    private LocalDateTime dateOfLastUse;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    private User ownerUser;

    @ManyToMany
    @JoinTable(name = "room_access",
            joinColumns = @JoinColumn(name = "room_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private List<User> usersWithAccess;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Size(min = 1, max = 32)
    @Column(length = 32)
    private String language = "Plain Text";

    @JsonManagedReference
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomMessage> messages;

    // region Constructors

    public Room() {
    }

    public Room(UUID id, String name, String language, User ownerUser, User... usersWithAccess) {
        this.id = id;
        this.name = name;
        this.ownerUser = ownerUser;
        this.dateOfLastUse = LocalDateTime.now();
        if(language != null && !language.isEmpty())
            this.language = language;

        this.ownerUser.getOwnedRooms().add(this);

        this.usersWithAccess = Stream.of(usersWithAccess).collect(Collectors.toList());
        this.usersWithAccess.forEach(x -> x.getAvailableRooms().add(this));
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
    public List<User> getUsersWithAccess() {
        return usersWithAccess;
    }

    /**
     * Get room name
     *
     * @return room name
     */
    public String getName() {
        return name;
    }

    public LocalDateTime getDateOfLastUse() {
        return dateOfLastUse;
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

    /**
     * Get programming language used in room
     *
     * @return room language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Get list of room messages
     *
     * @return list of room messages
     */
    public List<RoomMessage> getMessages() {
        return messages;
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

        usersWithAccess.remove(user);

        usersWithAccess.add(ownerUser);
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

    public void setDateOfLastUse(LocalDateTime dateOfLastUse) {
        this.dateOfLastUse = dateOfLastUse;
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

        if (usersWithAccess.contains(user))
            return false;

        usersWithAccess.add(user);
        user.getAvailableRooms().add(this);
        return true;
    }

    /**
     * Remove access to this room for user
     *
     * @param user user that will lose access to this room
     */
    public void removeUser(User user) {
        if (usersWithAccess == null)
            return;

        usersWithAccess.remove(user);
    }

    /**
     * Set room content
     *
     * @param content new content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Set room language
     * @param language new language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Add new message
     *
     * @param message message you want to add
     * @return true if message was added
     */
    public boolean addMessage(RoomMessage message) {
        if (messages == null)
            messages = new ArrayList<>();

        if (messages.contains(message))
            return false;

        messages.add(message);
        return true;
    }

    /**
     * Remove message
     *
     * @param message message you want to remove
     */
    public void removeMessage(RoomMessage message) {
        if (message == null)
            return;

        messages.remove(message);
    }

    // endregion

    // region Utils

    /**
     * Check if user can view room
     *
     * @param userId user id
     * @return true if user can view room
     */
    public boolean canBeViewedBy(String userId) {
        if (isOwnedBy(userId))
            return true;

        return isAvailableFor(userId);
    }

    /**
     * Check if room is owned by user
     *
     * @param userId user id
     * @return true if user is owner
     */
    public boolean isOwnedBy(String userId) {
        return ownerUser.getId().equals(userId);
    }

    /**
     * Check if user is in users with access list
     * (don't checks if user is owner)
     *
     * @param userId user id
     * @return true if user is in list
     */
    public boolean isAvailableFor(String userId) {
        return usersWithAccess.stream().anyMatch(user -> user.getId().equals(userId));
    }

    // endregion
}
