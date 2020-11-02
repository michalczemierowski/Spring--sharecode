package io.github.michalczemierowski.service;

import io.github.michalczemierowski.model.Room;
import io.github.michalczemierowski.model.User;
import io.github.michalczemierowski.repository.RoomRepository;
import io.github.michalczemierowski.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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


    public Optional<User> findUserByID(String email) {
        return userRepository.findById(email);
    }

    public Optional<Room> findRoomByID(UUID id) {
        return roomRepository.findById(id);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public Room saveRoom(Room room) {
        return roomRepository.save(room);
    }

    public void deleteUserById(String email) {
        userRepository.deleteById(email);
    }

    public void deleteRoomById(UUID id) {
        roomRepository.deleteById(id);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public List<Room> findRoomsByOwnerUser(String email)
    {
        return roomRepository.findByOwnerUserId(email);
    }

    public List<Room> findByRoomAccess_Id(String email)
    {
        return roomRepository.findByRoomAccess_Id(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
