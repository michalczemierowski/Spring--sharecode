package io.github.michalczemierowski.service;

import io.github.michalczemierowski.model.Room;
import io.github.michalczemierowski.model.RoomMessage;
import io.github.michalczemierowski.model.User;
import io.github.michalczemierowski.repository.RoomMessagesRepository;
import io.github.michalczemierowski.repository.RoomRepository;
import io.github.michalczemierowski.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DatabaseService {
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoomMessagesRepository roomMessagesRepository;

    @Autowired
    private SsePushNotificationService ssePushNotificationService;

    /**
     * Try to find user with specified id
     *
     * @param email user id
     * @return user if exists, else null
     */
    public Optional<User> findUserByID(String email) {
        return userRepository.findById(email);
    }

    /**
     * Try to find room with specified id
     *
     * @param id room id
     * @return room if exists, else null
     */
    public Optional<Room> findRoomByID(UUID id) {
        return roomRepository.findById(id);
    }

    /**
     * Save user in database
     *
     * @param user user you want to save
     * @return saved user if successful
     */
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Save room in database and send notifications
     *
     * @param room room you want to save
     * @return saved room if successful
     */
    public Room saveRoom(Room room) {
        // try to send update when saving room
        try {
            ssePushNotificationService.sendRoomUpdateNotification(room);
        } catch (Exception ignored) {
        }

        return roomRepository.save(room);
    }

    public RoomMessage saveRoomMessage(RoomMessage roomMessage) {
        return roomMessagesRepository.save(roomMessage);
    }

    /**
     * Delete user with specified id
     *
     * @param email user id
     */
    public void deleteUserById(String email) {
        userRepository.deleteById(email);
    }

    /**
     * Delete room with specified id
     *
     * @param id room id
     */
    public void deleteRoomById(UUID id) {
        roomRepository.deleteById(id);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    /**
     * Find list of rooms where owner id is equal to provided user id
     *
     * @param email user id
     * @return list of rooms where owner id equals provided user id
     */
    public List<Room> findRoomsByOwnerUser(String email) {
        return roomRepository.findByOwnerUserId(email);
    }

    /**
     * Find list of rooms where provided user is in room access list (not containing owned rooms)
     *
     * @param id user id
     * @return list of rooms where provided user is in room access list (not containing owned rooms)
     */
    public List<Room> findByUsersWithAccess_Id(String id) {
        return roomRepository.findByUsersWithAccess_Id(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
